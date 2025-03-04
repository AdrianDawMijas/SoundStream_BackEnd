import tensorflow.compat.v1 as tf
from magenta.models.melody_rnn import melody_rnn_sequence_generator
from magenta.models.shared import sequence_generator_bundle
import os

CHECKPOINT_DIR = "./checkpoints"  # Directorio donde están los datos de entrenamiento
OUTPUT_BUNDLE = "basic_rnn.mag"   # Archivo de salida

def create_bundle():
    if not os.path.exists(CHECKPOINT_DIR):
        print(f"⚠️ ERROR: La carpeta {CHECKPOINT_DIR} no existe.")
        return

    print("🔄 Creando el bundle de Magenta...")

    generator = melody_rnn_sequence_generator.MelodyRnnSequenceGenerator(
        details=None,
        checkpoint=CHECKPOINT_DIR,
        bundle=None
    )

    bundle = sequence_generator_bundle.SequenceGeneratorBundle(generator)
    bundle.save_to_file(OUTPUT_BUNDLE)

    print(f"✅ Bundle creado correctamente: {OUTPUT_BUNDLE}")

if __name__ == "__main__":
    create_bundle()
