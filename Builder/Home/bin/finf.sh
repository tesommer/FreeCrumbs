#/bin/bash

cp=""

function add_to_cp {
    if [ -z "$cp" ]; then
        cp=$1
    else
        cp="$cp:$1"
    fi
}

for i in `dirname $0`/../lib/*.jar; do
    if test -e $i; then
        add_to_cp $i
    fi
done

java -cp "$cp" freecrumbs.finf.Main $@
