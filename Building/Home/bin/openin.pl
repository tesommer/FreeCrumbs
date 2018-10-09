#!/usr/bin/perl

use strict;
use warnings;
use autodie;
use File::Basename;
use File::Temp qw/ tempfile tempdir /;

(my $script = basename($0)) =~ s/\.[^.]+$//;

my $help
= "Open in:\n"
. "Usage: $script <program>\n"
. "Writes STDIN to a tempfile, then invokes <program> with the tempfile as a\n"
. "command-line argument.\n";

if (scalar @ARGV == 1) {
    my ($fh, $filename) = tempfile();
    binmode(STDOUT);
    binmode(STDIN);
    foreach (<STDIN>) {
        print $fh $_;
    }
    `$ARGV[0] $filename`;
    close $fh;
    unlink $filename;
} else {
    print $help;
}
