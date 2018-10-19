#!/usr/bin/perl

use strict;
use warnings;
use File::Basename;

my $script = basename($0);

my $config
= "# This Perl script is used with Finf to print groups of file duplicates.\n"
. "# Example: $script | finf -c - MyDirectory | $script x\n"
. "# Here comes some Finf config:\n"
. "output=\${path}\${filename};\${md5}\${lf}\n";

my %hashes = ();

if (scalar @ARGV > 0) {
    foreach (<STDIN>) {
        if (/([^;]*);([^;]*)$/) {
            chomp(my $file = $1);
            chomp(my $hash = $2);
            if (exists $hashes{$hash}) {
                my $filesref = $hashes{$hash};
                push(@$filesref, $file);
            } else {
                my @files = ($file);
                $hashes{$hash} = \@files;
            }
        }
    }
    foreach my $hash (keys %hashes) {
        my $filesref = $hashes{$hash};
        if (scalar @$filesref > 1) {
            foreach my $file (@$filesref) {
                print "$file\n";
            }
            print "\n";
        }
    }
} else {
    print $config;
}
