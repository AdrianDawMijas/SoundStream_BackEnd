package com.iesvegademijas.soundstream_backend.utils;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.*;
import java.net.URL;

/**
 * Servicio de utilidad para descargar y recortar archivos de audio.
 * Utiliza la biblioteca JavaCV (FFmpeg) para procesar los datos multimedia.
 */
public class AudioTrimmerService {

    // Reduce la verbosidad del log de FFmpeg a errores únicamente
    static {
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
    }

    /**
     * Descarga y recorta un archivo de audio a partir de una URL remota.
     *
     * @param audioUrl           URL del archivo de audio.
     * @param maxDurationSeconds Duración máxima deseada del audio recortado (en segundos).
     * @return Archivo temporal con el audio recortado en formato MP3.
     * @throws Exception si falla la descarga o el procesamiento del audio.
     */
    public static File trimAudio(String audioUrl, double maxDurationSeconds) throws Exception {
        File downloadedFile = downloadAudioFile(audioUrl);
        return trimAudioFromFile(downloadedFile, maxDurationSeconds);
    }

    /**
     * Recorta un archivo de audio local a una duración máxima.
     *
     * @param inputFile          Archivo de entrada (audio original).
     * @param maxDurationSeconds Duración máxima permitida.
     * @return Archivo temporal MP3 recortado.
     * @throws Exception si ocurre un error en el procesamiento del audio.
     */
    private static File trimAudioFromFile(File inputFile, double maxDurationSeconds) throws Exception {
        File trimmedFile = File.createTempFile("trimmed-", ".mp3");

        try (
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
                FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(trimmedFile, grabber.getAudioChannels())
        ) {
            grabber.start();

            recorder.setFormat("mp3");
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioBitrate(grabber.getAudioBitrate());
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.start();

            Frame frame;
            double seconds = 0;

            // Copia muestras de audio mientras no se haya alcanzado la duración máxima
            while ((frame = grabber.grabSamples()) != null && seconds < maxDurationSeconds) {
                recorder.recordSamples(frame.samples);
                seconds = grabber.getTimestamp() / 1_000_000.0; // convierte microsegundos a segundos
            }

            recorder.stop();
            grabber.stop();

            System.out.println("✅ Audio recortado a " + seconds + " segundos.");
            return trimmedFile;

        } catch (Exception e) {
            System.err.println("❌ Error al recortar audio: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Descarga un archivo desde una URL a un archivo temporal local.
     *
     * @param url URL del archivo a descargar.
     * @return Archivo temporal con el contenido descargado.
     * @throws IOException si falla la conexión o la escritura del archivo.
     */
    private static File downloadAudioFile(String url) throws IOException {
        File tempFile = File.createTempFile("downloaded-", ".mp3");
        try (InputStream in = new URL(url).openStream();
             FileOutputStream out = new FileOutputStream(tempFile)) {
            in.transferTo(out);
        }

        System.out.println("📥 Archivo descargado: " + tempFile.getAbsolutePath());
        return tempFile;
    }
}
