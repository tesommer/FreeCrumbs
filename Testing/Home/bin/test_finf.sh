#!/bin/bash

RED=0

fail() {
    RED=1
    echo -e "${FUNCNAME[1]}:\n\t$1"
    return 1
}

zero_args_yields_success_and_no_output() {
    local OUTPUT
    OUTPUT=`finf` || fail "exit status"
    [ -z $OUTPUT ] || fail "output: $OUTPUT"
}

no_args_results_in_success_and_no_output

([ $RED -eq 0 ] && echo "GREEN") || echo "RED"
