#!/bin/sh

javadoc -d @dirjavadoc@/freecrumbs.finf \
        -sourcepath @dirsrc@/main/java/freecrumbs.finf \
        -subpackages freecrumbs.finf \
        --module-path @dirhome@/lib \

javadoc -d @dirjavadoc@/freecrumbs.macro \
        -sourcepath @dirsrc@/main/java/freecrumbs.macro \
        -subpackages freecrumbs.macro \
        --module-path @dirhome@/lib \

javadoc -d @dirjavadoc@/freecrumbs.macrec \
        -sourcepath @dirsrc@/main/java/freecrumbs.macrec \
        -subpackages freecrumbs.macrec \
        --module-path @dirhome@/lib \

grep '^#!' -l @dirhome@/bin/* | xargs chmod +x
tar -cvpzf @dirhome@.tar.gz @dirhome@
tar -cvpzf @dirjavadoc@.tar.gz @dirjavadoc@

zip -r @dirhome@.zip @dirhome@
zip -r @dirjavadoc@.zip @dirjavadoc@
