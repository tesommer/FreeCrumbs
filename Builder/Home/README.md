FreeCrumbs
==========

A collection of tiny command-line tools.

```
                                   //|\\      ///|\\\
                /////////|\\\\\  ///    \\ //        \\
             ///              \\\         //          \\
         /////                   \\     //       @    ||
        //                         \\  /             //
      ///////                       \//             //
    ///       \\\                                 //===
              //  \\\\                          //\\
             /////    \\                       //  \\
            //          \\\                   ||
                        //                    ||
                  //////                     //
           ///////                          //              FREE CRUMBS
             \\                          ////                    |
              \\\                       //                       V
                 \\\\\         \\\|/////
                      \\\\\\\\||      ||                 @ @@  @@@  @ @
                              ||     / |\
                             /|\    /  | \
                            / | \
```

License
-------

See **LICENSE.txt** for license information. (Third party libraries have their
own licenses.)

Directory layout
----------------

```
FreeCrumbs
    |
    +---- bin # executables
    |
    +---- lib # the FreeCrumbs jar and third party libraries
    |
    +---- FinfConfigs # configs that go to the Finf program (described below)
```

Programs included in FreeCrumbs
-------------------------------

* [Finf](#finf)
* [Dups](#dups)
* [Hash](#hash)

<a name="finf"></a>Finf
-----------------------

Finf is a command-line tool that prints file information to standard out. The
``finf`` command is located in the **bin** directory. There are two executables:

* **finf.bat** for Windows
* **finf** for Unix/GNU Linux

A line of file info may contain the following fields:

* *path*: the path without the filename (ends with a file separator)
* *filename*: the filename
* *size*: the size in bytes
* *modified*: the last modified timestamp
* *hash*: a checksum of the file's content

Two files with the exact same content will have the same hash. It's possible,
but extremely unlikely, that two different files will have equal hashes.

### Basic usage

The following examples assumes you're in a terminal or command prompt
with **bin** as the current directory.
___

    finf

Executing Finf without arguments does nothing.
___

    finf -h

This will print the Finf manual to standard out.
___

    finf C:\SomeDirectory

This will list the names of all files in **SomeDirectory**, including
subdirectories.
___

    finf -c ..\FinfConfigs\csv-list.properties C:\SomeDirectory

The ``-c`` option is used to specify a config file, in this case
**csv-list.properties**. This particular config makes Finf print each info line
as a semicolon-separated list on this format:

    path;filename;size;modified;hash

The output looks something like this:

    C:\MyDirectory\;file.zip;78480084;2016-12-19 00:48;adca82cebe0b276532fd6397a37a2c25
    C:\MyDirectory\SubDir1\;anotherfile.pdf;364772;2016-12-19 00:21;d63d564892ec3be256e66f903f90f9c3
    C:\MyDirectory\SubDir2\;yet_another_file.pdf;364772;2016-12-19 00:21;3559ec37d3d6fd604d754942ab2ed6b3

If the output is redirected to a CSV file …

    finf -c ..\FinfConfigs\csv-list.properties C:\SomeDirectory > myfile.csv

… it can be opened in a spread sheet, such as OpenOffice Calc.
___

    finf -c ..\FinfConfigs\lmod.properties C:\SomeDirectory

With **lmod.properties** Finf outputs the most recent modification date of all
the files in **SomeDirectory**.
___

    finf -c - C:\SomeDirectory

To read the config from standard in, use ``-`` (hyphen) instead of a config
file.
___

### Finf config files

The **FinfConfigs** directory contains some ready-to-use config files. A config
file is a text file on the Java *properties* format. Here's a sample file:

    hash.algorithm=SHA-256
    info.format=${path}${filename}: size: ${size}, last modified: ${modified}, hash: ${hash}
    date.format=yyyy-MM-dd HH:mm
    file.filter=.*\.html
    order=filename size asc modified desc
    count=100

* ``hash.algorithm`` is the algorithm used to generate the file hash. Default is
  MD5.

* ``info.format`` is the format of the output lines. Occurrences of the
  following tokens will be replaced by the corresponding actual information:
  *${path}*, *${filename}*, *${size}*, *${modified}* and *${hash}*.

* ``date.format`` specifies the format of the modified timstamp in the output.
  See *java.txt.SimpleDateFormat* for details.

* ``file.filter`` is a regex that matches the filenames to include as input. If
  absent, all files are included.

* ``order`` specifies a sort order of the output lines. It has the fields to
  order by (path, filename, etc), with space in between. Each field may be
  followed by ``asc`` or ``desc`` (ascending or descending). ``asc`` is default.

* ``count`` sets a maximum number of output lines. If absent, all lines are
  output.

If Finf doesn't get a config file, it will use this:

    hash.algorithm=MD5
    info.format=${filename}
    date.format=yyyy-MM-dd HH:mm

### Specifying config settings on the command-line

    finf -o "info.format=${hash}" -o "hash.algorithm=SHA-1" hypotheticalfile.zip

The ``-o`` option allows you to override a config setting. In this case no
config file was given. But if the ``-c`` option is used, a setting given with
``-o`` trumps the same setting in the config file. The example above prints the
SHA-1 checksum for **hypotheticalfile.zip**.

<a name="dups"></a>Dups
-----------------------

The **bin** directory contains a file named **dups.pl**. This is a Perl script
that uses Finf to print groups of duplicate files to standard out. With no
arguments it prints a Finf config file. With any argument it parses the output
from finf and prints the result. This script is used together with Finf with the
help of [pipelines](https://en.wikipedia.org/wiki/Pipeline_%28Unix%29).

Example:

    dups.pl | finf -c - \LotsOfStuff | dups.pl x

Sample output:

    \LotsOfStuff\Pix\20150311(1).jpg
    \LotsOfStuff\OldStuff\20150311(1).jpg

    \LotsOfStuff\Paper\draft10.odf
    \LotsOfStuff\Paper\paper.odf
    \LotsOfStuff\Trash\draft10.odf

<a name="hash"></a>Hash
-----------------------

Hash is a program that prints file checksums to standard out. There are two
executables in **bin**:

* **hash.bat** for Windows
* **hash** for Unix/GNU Linux

Usage:

    hash <file> [algorithm list]

Examples:
___

    hash some_file.zip

Prints the MD5, SHA-1 and SHA-256 checksums of **some_file.zip**. Sample output:

    MD5: 6bdc31914080db372341262e06ff8ea8
    SHA-1: d4d34b1d5434fdfa77909e3c2fa92b7f50489425
    SHA-256: c7a98f6a6e6bdeeb29139b18155d8c24fa940f63bad810f0622a7b20c06f2129

___

    hash xyz.iso md5 sha-512

Prints the MD5 and SHA-512 checksums of **xyz.iso**.
___

*Copyright &copy; 2017 Tone Sommerland*
