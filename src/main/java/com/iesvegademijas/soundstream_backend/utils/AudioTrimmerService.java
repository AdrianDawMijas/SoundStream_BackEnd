package com.iesvegademijas.soundstream_backend.utils;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.*;
import java.net.URL;

public class AudioTrimmerService {

    static {
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
    }

    /**
     * Recorta un audio a partir de una URL remota.
     */
    public static File trimAudio(String audioUrl, double maxDurationSeconds) throws Exception {
        File downloadedFile = downloadAudioFile(audioUrl);
        return trimAudioFromFile(downloadedFile, maxDurationSeconds);
    }

    /**
     * Recorta un archivo de audio local.
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

            while ((frame = grabber.grabSamples()) != null && seconds < maxDurationSeconds) {
                recorder.recordSamples(frame.samples);
                seconds = grabber.getTimestamp() / 1_000_000.0;
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
     * Descarga un archivo desde una URL a un archivo temporal.
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
