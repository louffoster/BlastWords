#!/bin/bash
java -classpath blastwords/libs/gdx.jar:blastwords/libs/gdx-tools.jar com.badlogic.gdx.tools.imagepacker.TexturePacker2 blastwords-android/raw/ . game_atlas

echo -n "Moving assets into place..."
mv game_atlas* blastwords-android/assets/data/

echo "DONE!"
