from flask import Flask, request, jsonify
import magenta.music as mm
from magenta.models.melody_rnn import melody_rnn_model
from magenta.models.melody_rnn import melody_rnn_sequence_generator as melody_rnn
import tensorflow.compat.v1 as tf
import os

app = Flask(__name__)

# Cargar el modelo de Magenta
CHECKPOINT = "basic_rnn.mag"  # Asegúrate de que este archivo existe
generator = None

def load_generator():
    """Carga el modelo de generación de música"""
    global generator
    if not os.path.exists(CHECKPOINT):
        print(f"⚠️ ERROR: El archivo {CHECKPOINT} no existe.")
        return

    bundle = mm.sequence_generator_bundle.read_bundle_file(CHECKPOINT) if hasattr(mm, "sequence_generator_bundle") else None
    model = melody_rnn_model.MelodyRnnModel()

    generator = melody_rnn.MelodyRnnSequenceGenerator(
        model=model,
        details=mm.protobuf.generator_pb2.GeneratorDetails(id="basic_rnn"),
        checkpoint=None,
        bundle=bundle
    )
    print("✅ Modelo de Magenta cargado correctamente.")

@app.route('/generate-music', methods=['POST'])
def generate_music():
    """Endpoint para generar música"""
    data = request.json
    song_name = data.get("name", "default_song")
    duration = data.get("duration", 1) * 60  # Minutos a segundos
    tempo = data.get("tempo", 120)

    file_path = f"output/{song_name.replace(' ', '_')}.mid"

    try:
        generate_with_magenta(file_path, duration, tempo)
        return jsonify({"message": "Música generada", "file": file_path})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

def generate_with_magenta(file_path, duration, tempo):
    """Genera música con Magenta y guarda un archivo MIDI"""
    global generator
    if generator is None:
        load_generator()
        if generator is None:
            raise RuntimeError("El generador no pudo inicializarse.")

    # Crear una secuencia inicial
    primer_midi = "primer.mid"
    if not os.path.exists(primer_midi):
        raise FileNotFoundError(f"El archivo {primer_midi} no existe.")

    primer_sequence = mm.midi_io.note_sequence_from_midi_file(primer_midi)

    # Configurar la generación
    generator_options = generator.get_default_generator_options()
    generator_options.args["temperature"].float_value = 1.0  # Ajuste de creatividad

    # Generar la música
    sequence = generator.generate(primer_sequence, generator_options)

    # Guardar el archivo MIDI generado
    os.makedirs(os.path.dirname(file_path), exist_ok=True)
    mm.midi_io.sequence_proto_to_midi_file(sequence, file_path)
    print(f"✅ Archivo MIDI generado: {file_path}")

if __name__ == '__main__':
    load_generator()
    app.run(host="0.0.0.0", port=5000)
