#!/bin/bash

RED=0
ME="$0"
MY_DIR="`dirname $ME`"
MY_FILENAME="${ME#$MY_DIR/}"
HOME_DIR="$MY_DIR/.."
LIB_DIR="$HOME_DIR/lib"

finf() {
    $MY_DIR/finf "$@"
}

fail() {
    RED=1
    echo -e "${FUNCNAME[1]}:\n\t$1"
    return 1
}

zero_args_yields_success_and_no_output() {
    local OUTPUT
    OUTPUT=`finf` || fail "exit status"
    [ -z "$OUTPUT" ] || fail "output: $OUTPUT"
}

exit_status_is_nonzero_on_error() {
    finf -o 'filter=*' 2> /dev/null && fail "exit status"
}

default_config_just_outputs_filenames() {
    local OUTPUT
    OUTPUT=`finf $ME` || fail "exit status"
    [ "$OUTPUT" = "$MY_FILENAME" ] || fail "output: $OUTPUT"
}

config_setting_can_be_overridden_with_option() {
    local OUTPUT
    OUTPUT=`finf -o output=Abc $ME` || fail "exit status"
    [ "$OUTPUT" = "Abc" ] || fail "output: $OUTPUT"
}

omitting_overridden_value_reverts_setting_to_default() {
    local OUTPUT
    OUTPUT=`echo "output=Xyz" | finf -c - -o "output" $ME`
    [ "$OUTPUT" = "$MY_FILENAME" ] || fail "output: $OUTPUT"
}

order_setting_is_a_list_of_fields_to_order_by() {
    local OUTPUT
    local EXPECTED
    OUTPUT=`finf -o 'order=path filename' "$LIB_DIR"`
    EXPECTED=`ls -1 "$LIB_DIR"`
    [ "$OUTPUT" = "$EXPECTED" ] || fail "output: $OUTPUT expected: $EXPECTED"
}

fields_in_order_setting_can_have_tailing_asc_or_desc() {
    local OUTPUT
    OUTPUT=`finf -o 'order=path asc filename desc' "$LIB_DIR"`
    EXPECTED=`ls -1 -r "$LIB_DIR"`
    [ "$OUTPUT" = "$EXPECTED" ] || fail "output: $OUTPUT expected: $EXPECTED"
}

regex_filter_setting_excludes_input_files_with_names_matching_regex() {
    local OUTPUT
    OUTPUT=`finf -o 'filter=.*calclipse.*' "$LIB_DIR"`
    [ "${OUTPUT:4:9}" = "calclipse" ] || fail "output: $OUTPUT"
}

multiple_filters_are_supported_by_appending_dot_and_something_to_their_keys() {
    local OUTPUT
    OUTPUT=`finf -o 'filter.1=.*jar' -o 'filter.2=.*calclipse.*' "$HOME_DIR"`
    [ "${OUTPUT:4:9}" = "calclipse" ] || fail "output: $OUTPUT"
}

filter_setting_supports_format_pattern_style() {
    local OUTPUT
    OUTPUT=`finf -o 'filter=<filename>++finf.*--.*\.bat' "$HOME_DIR"`
    [ "$OUTPUT" = "finf" ] || fail "output: $OUTPUT"
}

date_format_setting_is_applied_to_time_fields() {
    local OUTPUT
    OUTPUT=`finf -o 'date.format=yyyy-MM' -o 'output=<modified>' "$ME"`
    [[ "$OUTPUT" =~ [0-9]+-[0-9]{2} ]] || fail "output: $OUTPUT"
}

empty_date_format_turns_timestamp_formatting_of() {
    local OUTPUT
    OUTPUT=`finf -o 'date.format=' -o 'output=<modified>' "$ME"`
    [[ "$OUTPUT" =~ [0-9]+ ]] || fail "output: $OUTPUT"
}

eolcount_field_is_the_number_of_line_terminators() {
    local OUTPUT
    OUTPUT=`finf -o 'output=<eolcount> <path><filename>' "$ME"`
    EXPECTED=`wc -l "$ME"`
    [ "$OUTPUT" = "$EXPECTED" ] || fail "output: $OUTPUT expected: $EXPECTED"
}

zero_args_yields_success_and_no_output
exit_status_is_nonzero_on_error
default_config_just_outputs_filenames
config_setting_can_be_overridden_with_option
omitting_overridden_value_reverts_setting_to_default
order_setting_is_a_list_of_fields_to_order_by
fields_in_order_setting_can_have_tailing_asc_or_desc
regex_filter_setting_excludes_input_files_with_names_matching_regex
multiple_filters_are_supported_by_appending_dot_and_something_to_their_keys
filter_setting_supports_format_pattern_style
date_format_setting_is_applied_to_time_fields
empty_date_format_turns_timestamp_formatting_of
eolcount_field_is_the_number_of_line_terminators

([ $RED -eq 0 ] && echo "GREEN") || echo "RED"
