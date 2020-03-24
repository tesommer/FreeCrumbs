#!/bin/sh

grep '^#!' -l @dirhome@/bin/* | xargs chmod +x
tar -cvpzf @dirhome@.tar.gz @dirhome@
zip -r @dirhome@.zip @dirhome@
