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

Legal
-----

Copyright &copy; 2017 Tone Sommerland

For the terms and conditions of this product, see **LICENSE.txt**.

By acquiring and/or using this product, you also accept the licenses of any
third-party libraries that this product depends on.

FreeCrumbs comes bundled with:

* [CalclipseLib version 2](http://www.calclipse.com)

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
* [Macro](#macro)

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

### Specifying config settings on the command line

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

<a name="macro"></a>Macro
-------------------------

Macro is an interpreter for a tiny scripting language that automates human-like
interaction with the computer: key strokes, mouse moves, etc. Macro utilizes
Java's robot API to inject events into the system event queue. The **bin**
directory contains launchers for Macro:

* **macro.bat** for Windows
* **macro** for Unix/GNU Linux

### Basic usage:

The procedure for executing a macro script is straight forward:

    macro <script-file>

The ``macro`` command permits a couple of options:

* ``-m`` followed by a macro name executes a named macro within the script.
* ``-t`` is followed by the number of times to run a named macro. One is
  default. If this option is specified, ``-m`` must also be.
* ``-h`` prints a help message.

### Macro scripts

A script may contain an arbitrary number of macros. A macro is terminated by a
blank line or the end-of-file. Comments are lines that start with ``#``. The
macro name is specified like this: ``name MyMacro``.

This is an example macro script:

    # This macro tabs between windows.
    name WTAB
    add_key_code_variables
    key_press VK_ALT
    key_press VK_TAB
    key_release VK_TAB
    key_release VK_ALT

**Warning:**
*A key press must have a corresponding key release. Similarly, a mouse button
press must be paired with a release of the button.*

#### Macro script reference

This is a list of macro script commands. A script allows integer-variable
declarations. For any parameter written in &lt;angles&gt;, either an integer
literal or script variable may be used.

* ``add_key_code_variables``:
  Creates script variables corresponding to constants in
  *java.awt.event.KeyEvent* (VK_A, VK_ALT, VK_SPACE, …).

* ``delay <millis>``: Delays further execution a specified number of
  milliseconds.

* ``exit``: Exits the script.

* ``image_xy x-variable y-variable image-file``: Stores the coordinates of an
  image within the current screen capture to script variables. The image
  file may be relative to the script location. If the image was not on screen,
  both variables will be set to -1.

* ``key_press <key-code>``: Generates a key press event.

* ``key_release <key-code>``: Generates a key release event.

* ``mouse_move <x> <y>``: Moves the mouse to specified x-y coordinates.

* ``mouse_press <button1> [<button2> [<button3>]]``: Generates a mouse press
  event. A button is an integer where nonzero is pressed and zero is not.
  Buttons are numbered left to right. In other words, to press the middle
  button: ``mouse_press 0 1``. Mouse buttons must be released with the
  ``mouse_release`` command.

* ``mouse_release <button1> [<button2> [<button3>]]``: The release analogue to
  ``mouse_press``.

* ``mouse_wheel <steps>``: Moves the mouse wheel. Negative steps means up/away
  from user.

* ``play macro-name [<times>]``: Plays the macro with the given name a certain
  number of times (default is one time). This command supports an optional
  logical expression, e.g.: ``play WTAB 1 x > -1``. The macro will be played if
  the condition is true. The following logical operators are supported:
    * ``==``: equals
    * ``!=``: not equals
    * ``<``: less than
    * ``<=``: less than or equals
    * ``>``: greater than
    * ``>=``: greater than or equals

* ``print output``: Prints output to STDOUT. Script variables may be referenced
  by precedeing them with $ in the output.

* ``set variable <value>``: Sets or creates a script variable. The variable may
  be assigned an arithmetic expression, e.g.: ``set xysum x + y``. Supported
  operators are:
    * ``+``: addition
    * ``-``: subtraction
    * ``*``: multiplication
    * ``/``: division
    * ``%``: modulus (remainder of integer division)

* ``type <value>``: Generates key presses and key releases that types the given
  value.
