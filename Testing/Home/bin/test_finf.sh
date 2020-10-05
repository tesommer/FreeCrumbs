#!/bin/bash

RED=0
ME="$0"
MY_DIR="`dirname $ME`"
MY_FILENAME="${ME#$MY_DIR/}"
HOME_DIR="$MY_DIR/.."
LIB_DIR="$HOME_DIR/lib"

finf()
{
    $MY_DIR/finf "$@"
}

fail()
{
    RED=1
    echo -e "${FUNCNAME[-2]}:\n\t$1"
    return 1
}

assert_equals_str()
{
    [ "$1" = "$2" ] || fail "expected: $1, actual $2"
}

assert_equals_int()
{
    [ "$1" -eq "$2" ] || fail "expected: $1, actual $2"
}

zero_args_yields_success_and_no_output()
{
    ACTUAL=`finf` || fail "exit status"
    [ -z "$ACTUAL" ] || fail "output: $ACTUAL"
}

exit_status_is_nonzero_on_error()
{
    finf -o 'filter=*' 2> /dev/null && fail "exit status"
}

default_config_just_outputs_filenames()
{
    ACTUAL=`finf $ME` || fail "exit status"
    assert_equals_str "$MY_FILENAME" "$ACTUAL"
}

config_setting_can_be_overridden_with_option()
{
    ACTUAL=`finf -o output=Abc $ME` || fail "exit status"
    assert_equals_str "Abc" "$ACTUAL"
}

omitting_overridden_value_reverts_setting_to_default()
{
    ACTUAL=`echo "output=Xyz" | finf -c - -o "output" $ME`
    assert_equals_str "$MY_FILENAME" "$ACTUAL"
}

order_setting_is_a_list_of_fields_to_order_by()
{
    EXPECTED=`ls -1 "$LIB_DIR"`
    ACTUAL=`finf -o 'order=path filename' "$LIB_DIR"`
    assert_equals_str "$EXPECTED" "$ACTUAL"
}

fields_in_order_setting_can_have_trailing_asc_or_desc()
{
    EXPECTED=`ls -1 -r "$LIB_DIR"`
    ACTUAL=`finf -o 'order=path asc filename desc' "$LIB_DIR"`
    assert_equals_str "$EXPECTED" "$ACTUAL"
}

regex_filter_setting_excludes_input_files_with_names_matching_regex()
{
    ACTUAL=`finf -o 'filter=.*calclipse.*' "$LIB_DIR"`
    [ "${ACTUAL:4:9}" = "calclipse" ] || fail "output: $ACTUAL"
}

multiple_filters_are_supported_by_appending_dot_and_something_to_their_keys()
{
    ACTUAL=`finf -o 'filter.1=.*jar' -o 'filter.2=.*calclipse.*' "$HOME_DIR"`
    [ "${ACTUAL:4:9}" = "calclipse" ] || fail "output: $ACTUAL"
}

filter_setting_supports_format_pattern_style()
{
    ACTUAL=`finf -o 'filter=<filename>++finf.*--.*\.bat' "$HOME_DIR"`
    [ "$ACTUAL" = "finf" ] || fail "output: $ACTUAL"
}

date_format_setting_is_applied_to_time_fields()
{
    ACTUAL=`finf -o 'date.format=yyyy-MM' -o 'output=<modified>' "$ME"`
    [[ "$ACTUAL" =~ [0-9]+-[0-9]{2} ]] || fail "output: $ACTUAL"
}

empty_date_format_turns_timestamp_formatting_of()
{
    ACTUAL=`finf -o 'date.format=' -o 'output=<modified>' "$ME"`
    [[ "$ACTUAL" =~ [0-9]+ ]] || fail "output: $ACTUAL"
}

eolcount_field_is_the_number_of_line_terminators()
{
    EXPECTED=`wc -l "$ME"`
    ACTUAL=`finf -o 'output=<eolcount> <path><filename>' "$ME"`
    assert_equals_str "$EXPECTED" "$ACTUAL"
}

zero_args_yields_success_and_no_output
exit_status_is_nonzero_on_error
default_config_just_outputs_filenames
config_setting_can_be_overridden_with_option
omitting_overridden_value_reverts_setting_to_default
order_setting_is_a_list_of_fields_to_order_by
fields_in_order_setting_can_have_trailing_asc_or_desc
regex_filter_setting_excludes_input_files_with_names_matching_regex
multiple_filters_are_supported_by_appending_dot_and_something_to_their_keys
filter_setting_supports_format_pattern_style
date_format_setting_is_applied_to_time_fields
empty_date_format_turns_timestamp_formatting_of
eolcount_field_is_the_number_of_line_terminators

[ "$RED" -eq "0" ] && echo "GREEN" || echo "RED"
