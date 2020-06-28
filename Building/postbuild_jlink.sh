#!/bin/sh

ADD_MODULES=`ls @dirhome@/lib/ | sed -e 's/-.*\.jar//' | tr '\n' ','`
ADD_MODULES=${ADD_MODULES%?}

jlink --output @dirhome@/jre \
      --module-path @dirhome@/lib \
      --add-modules $ADD_MODULES

LBIN=`grep -l '^#!' @dirhome@/bin/*`

sed -e 's:java:"$MY_DIR"/../jre/bin/java:g' -i $LBIN
