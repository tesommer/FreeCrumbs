FreeCrumbs
==========

A tiny collection of command-line tools.

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

_**Note:** Requires Perl and Java 11._

Legal
-----

Copyright &copy; @copyrightyearowner@

For the terms and conditions of this product, see **LICENSE.txt**.

By acquiring and/or using this product, you also accept the licenses of any
third-party libraries that this product depends on.

FreeCrumbs comes bundled with:

* [CalclipseLib](http://www.calclipse.com)

Directory layout
----------------

```
FreeCrumbs
    |
    +---- bin # executables
    |
    +---- lib # the FreeCrumbs jars and third-party libraries
    |
    +---- FinfConfigs # configs that go to the Finf program (described below)
```

Included in FreeCrumbs
----------------------

* [Finf](#finf)
* [Macro](#macro)
    * [Macrec](#macrec)
* [Tiny crumbs](#tinycrumbs)
    * [dups](#dups)
    * [openin](#openin)

<a name="finf"></a>Finf
-----------------------

Finf is a command-line tool that prints file information to standard output. The
``finf`` command is located in the **bin** directory. There are two executables:

* **finf.bat** for Windows
* **finf** for Unix and GNU/Linux

A file-info unit might contain the following fields:

* *path*: the path without the filename (ends with a file separator)
* *filename*: the filename
* *size*: the size in bytes
* *modified*: the last-modified timestamp
* *md5*: MD5 checksum
* *sha-1*: SHA-1 checksum
* *sha-256*: SHA-256 checksum
* *eol*: system line-terminator
* *cr*: carriage return
* *lf*: line feed
* *crlf*: carriage-return-line-feed
* *eolcount*: number of line terminators
* *crcount*: number of carriage returns
* *lfcount*: number of line feeds
* *crlfcount*: number of carriage-return-line-feeds
* *class*: heuristical classification of the file as either EMPTY, TEXT or
  BINARY
* *space*: a space character
* *tab*: a tab character
* *hex*: binary to lowercase hexadecimal\n"
* *HEX*: binary to uppercase hexadecimal\n"
* *base64*: binary to base64\n"
* *base64mime*: binary to multiline base64\n"
* *base64url*: binary to URL/filename-safe base64\n"

### Basic usage

The following examples assume you're in a terminal or command prompt with
**bin** as the current directory.
___

    finf

Executing Finf without arguments does nothing.
___

    finf -h

This will print Finf documentation to standard output.
___

    finf C:\SomeDirectory

This will list the names of all files in **SomeDirectory**, including
subdirectories.
___

    finf -c ..\FinfConfigs\csv-list.properties C:\SomeDirectory

The ``-c`` option is used for specifying a config file, in this case
**csv-list.properties**. This particular config makes Finf print each info unit
as a semicolon-separated list on this format:

    path;filename;size;modified;md5

The output looks something like this:

    C:\MyDirectory\;file.zip;78480084;2016-12-19 00:48;adca82cebe0b276532fd6397a37a2c25
    C:\MyDirectory\SubDir1\;anotherfile.pdf;364772;2016-12-19 00:21;d63d564892ec3be256e66f903f90f9c3
    C:\MyDirectory\SubDir2\;yet_another_file.pdf;364772;2016-12-19 00:21;3559ec37d3d6fd604d754942ab2ed6b3

If the output is redirected to a CSV file &hellip;

    finf -c ..\FinfConfigs\csv-list.properties C:\SomeDirectory > myfile.csv

&hellip; it can be opened in a spread sheet, such as OpenOffice Calc.
___

    finf -c ..\FinfConfigs\lmod.properties C:\SomeDirectory

With **lmod.properties** Finf outputs the most recent modification date of all
the files in **SomeDirectory**.
___

    finf -c - C:\SomeDirectory

To read the config from standard input, use ``-`` (hyphen) instead of a config
file.
___

### Finf config files

The **FinfConfigs** directory contains some ready-to-use config files. A config
file contains parameters specified as key-value pairs.

#### Sample config file:

```
# Searches a Git repository for scripts with both shebang and CRLF line-endings.

# exclude the .git dir
filter.0=<path>--.*/\.git/.*

# include only non-empty text files
filter.1=<class>++TEXT

# include files containing CRLF
filter.2=<crlfcount>--0

# search for a shebang
var.0=/^#!.*$/

# include files containing a shebang
filter.3=<var.0.found>++1

# output each file along with the shebang they contain
output=<path><filename><space><var.0.input><eol>
```

#### Config settings

* ``output`` is the format of the outputted info. Occurrences of tokens on the
  form *&lt;field&gt;* (such as *&lt;path&gt;*, *&lt;filename&gt;* and so on)
  will be replaced by the corresponding field value.

* ``order`` specifies a sort order of the outputted info. It has the fields to
  order by (path, filename, etc), with space in between. Each field may be
  followed by ``asc`` or ``desc`` (ascending or descending). ``asc`` is default.

* ``filter`` filters input files. If absent, all files are included. This
  setting may be on one of two forms:
  * _regex_: a regex pattern that matches the filenames to include as input
  * _format pattern_: an info format followed by one or more regex patterns,
    each preceded by either ``++`` to include matches or ``--`` to exclude
    matches. The format is applied to the file info, and the result is matched
    against the patterns. For example, the format pattern  
    ``<filename>++.+\.html?--index\..{3,4}``  
    includes files with extension _htm_ and _html_, but not index files.

* ``count`` sets a maximum number of outputted info units. If absent, all are
  outputted.

* ``hash.algorithms`` is a whitespace-separated list of hash algorithms. Each
  algorthm will be available as an info field with the name being the algorithm
  in lowercase.

* ``date.format`` specifies the format applied to timestamp values. See
  *java.txt.SimpleDateFormat* for details. An empty date format turns timestamp
  formatting off.

* ``prefilter`` specifies whether or not to filter files before acquiring field
  values needed by other settings. Prefiltering is turned on by defult. A value
  of ``0`` turns it off. When off, the values of all fields referenced in the
  config will be acquired collectively for each file.

* ``var`` specifies parameters for dynamic info fields. The following types are
  supported:
  
    * *search*
    * *command*
  
  The *search* type specifies parameters for a search in the files' content for
  a match against a regex pattern. The format of a search is:  
  ``/regex/o=occurrence,g=groups,c=charset``  
  ``occurrence`` is the occurrence to search for (default is 1). A negative
  occurrence searches from the bottom rather than the top. An occurrence of zero
  results in not found. ``groups`` is the number of regex groups to include
  (default is 0). ``charset`` is the character encoding to apply (default is the
  default local charset). A search makes the following fields available, but
  prefixed with this setting's key and a period (.):
  
    * ``found``: 0 or 1 depending on whether a match was found or not
    * ``groupcount``: number of regex groups, excluding group zero
    * ``line``: one-based line number of matched regex (-1 if not found)
    * ``input``: the matched input sequence (empty if not found)
    * ``start``: zero-based char-index of the start of the matched sequence (-1
      if not found)
    * ``end``: the first zero-based char index after the matched sequence (-1 if
      not found)
  
  For each included group, the following fields will be available, but prefixed
  with this setting's key, a period (.), the group number and a hyphen (-):
  
    * ``line`` one-based line number of matched group (-1 if not found)
    * ``start`` zero-based char index of the start of the matched group (-1 if
      not found)
    * ``end`` the first zero-based char index after the matched group (-1 if not
      found)
  
  If included groups exceed the group count, excess groups will be not found.
  
  The *command* type specifies parameters for an external command to be executed
  for each file. The format is:  
  `` `command1|command2|...` ``  
  A command execution makes the following fields available, but prefixed with
  this setting's key and a period (.):
  
    * ``count`` number of commands in the pipeline
    * ``status`` the execution's exit status
    * ``out`` the execution's ouput to STDOUT
    * ``err`` the execution's ouput to STDERR
    * ``pid`` the execution's process ID
  
  For each command preceding the last command in a pipeline, the fields will be
  prefixed with this setting's key, a period (.), the command number and a
  hyphen (-).

Certain settings can have multiple instances specified in the same config. This
is accomplished by appending a period (.) and an optional suffix to their
standard keys, thereby forming unique keys. These settings are applied in sort
order of their keys. The following settings support this:

 * filter
 * var

Example: 
```
filter=.*.txt
filter.nonempty=<class>--EMPTY
```

If Finf doesn't get a config file, it'll use this:

    hash.algorithms=MD5 SHA-1 SHA-256
    output=<filename><eol>
    date.format=yyyy-MM-dd HH:mm
    prefilter=1

### Specifying config settings on the command line

    finf -o "output=<sha-512><eol>" -o "hash.algorithms=SHA-512" hypotheticalfile.zip

The ``-o`` option allows you to override a config setting. In this case no
config file was given. But if the ``-c`` option is used, a setting given with
``-o`` trumps the same setting in the config file. The example above prints the
SHA-512 checksum for **hypotheticalfile.zip**.

Omitting ``=value`` unsets the setting (and thus reverts to the default):

    finf -c csv-list.properties -o order

<a name="macro"></a>Macro
-------------------------

Macro is an interpreter for a tiny scripting language that automates human-like
interaction with the computer: key strokes, mouse moves, etc. Macro utilizes
Java's robot API to inject events into the system event queue. The **bin**
directory contains launchers for Macro:

* **macro.bat** for Windows
* **macro** for Unix and GNU/Linux

### Basic usage:

The procedure for executing a macro script is straight forward:

    macro script-file

The ``macro`` command permits a couple of options:

* ``-t`` is followed by the number of times to play. One is default.
* ``-m`` followed by a macro name plays a named macro within the script.
* ``-h`` prints a help message.

If a macro name is not specified, the first macro in the script is played.

### Macro scripts

A script may contain an arbitrary number of macros. A macro is terminated by a
blank line or the end-of-file. Comments are lines that start with ``#``. The
macro name is specified like this: ``name MyMacro``.

This is a sample macro script:

    # This macro tabs between windows.
    name WTAB
    add_key_code_variables
    key_press VK_ALT
    key_press VK_TAB
    key_release VK_TAB
    key_release VK_ALT

**Warning:**
*A key press must have a corresponding key release. Similarly, a mouse-button
press must be paired with a release of the button.*

#### Variables

A script may contain two kinds of variables: integers and images. They have
separate namespaces. The variables created in a script only persist during the
given run of the script, regardless of which macros are played.

#### Script- and image locations

Locations referencing external scripts and images use forward slash (/). They
may be relative to the executing script.

#### Macro-script reference

This is a list of macro-script commands. For any integer parameter written in
&lt;angles&gt;, either an integer literal or variable name may be used. For any
image parameter in angles, either an image variable or location may be used.
Optional parameters are surrounded by [square brackets]. A parameter followed by
equals and a value denotes a default value for the parameter.

* ``add_key_code_variables``:
  Creates script variables corresponding to constants in
  *java.awt.event.KeyEvent* (VK_A, VK_ALT, VK_SPACE, &hellip;).

* ``beep``:
  Creates an audible alert.

* ``delay <millis> [auto]``:
  Delays further execution a specified number of milliseconds or sets the auto
  delay.

* ``exit``:
  Exits the script.

* ``idle [auto [<onOffToggle>=1]]``:
  Waits for idle or sets the auto wait-for-idle. ``onOffToggle`` is zero to turn
  auto wait-for-idle off, greater than zero to turn it on and less than zero to
  toggle.

* ``key_chord <key-code1> <key-code2> ...``:
  Generates a key chord by pressing one or more keys, then releasing them in
  reverse order.  
  Example: ``key_chord VK_CONTROL VK_C``

* ``key_press <key-code>``:
  Generates a key-press event.

* ``key_release <key-code>``:
  Generates a key-release event.

* ``load variable location``:
  Loads an image from the specified location and stores it as a script image
  with the given name.

* ``mouse_move <x> <y>``:
  Moves the mouse to specified x-y coordinates.

* ``mouse_press <button1> [<button2> [<button3>]]``:
  Generates a mouse-press event. A button is an integer where nonzero is pressed
  and zero is not. Buttons are numbered left to right. In other words, to press
  the middle button:  
  ``mouse_press 0 1``. Mouse buttons must be released with the ``mouse_release``
  command.

* ``mouse_release <button1> [<button2> [<button3>]]``:
  The release analogue to ``mouse_press``.

* ``mouse_wheel <steps>``:
  Moves the mouse wheel. Negative steps means up/away from user.

* ``pixel variable <x> <y>``:
  Samples a pixel at ``x`` and ``y``, and stores its RGB value in the given
  variable.

* ``play macro [<times>=1]``:
  Plays a macro a certain number of times (default is one). ``macro`` specifies
  a macro in the current script, or a macro in an external script. In the latter
  case, the format of the parameter is ``script-location->macro-name``. The
  macro name may be omitted to play the first macro in the script. Input can be
  passed to the external script like this:  
  ``script-location:variable1=<value1>:variable2=<value2>->macro-name``.  
  The variable-value pairs will be set in the external script. This command
  supports an optional logical expression, e.g.:  
  ``play WTAB 1 x > -1``. The macro will be played if the condition is true. The
  following logical operators are supported:
    * ``==``: equals
    * ``!=``: not equals
    * ``<``: less than
    * ``<=``: less than or equals
    * ``>``: greater than
    * ``>=``: greater than or equals
    * ``isset``: tests the existence of a variable.
      ``var isset 1`` is true if ``var`` has been set.
      ``var isset 0`` is true if ``var`` has not been set yet.

* ``print output``:
  Prints output to STDOUT. Script variables may be referenced by precedeing them
  with $ in the output.

* ``scan <from-x> <from-y> <to-x> <to-y> x-variable y-variable <image>
  [<occurrence>=1 [<delay>=0 [<times>=1 [success-macro-name
  [failure-macro-name]]]]]``:
  Scans the current screen capture for an image. Stores the coordinates of the
  image to script variables. If the image is not located, both variables will be
  set to -1. Occurrences are counted from the top. This command will wait
  ``delay`` milliseconds, take a screen shot and search for the image. It will
  do this ``times`` times. If the image was found, ``success-macro-name`` will
  be played, if specified. Otherwise, ``failure-macro-name`` will be played, if
  specified. The from/to parameters limits the region being scanned. A value of
  -1 for any of the from/to parameters disregards the limitation of the
  parameter. If any of the from/to parameters are out of bounds, it is
  restricted automatically.

* ``screenshot variable [<x> [<y> [<width> [<height>]]]]``:
  Takes a screenshot of the current screen and stores it as an image variable.
  The parameters default to the screen dimensions.

* ``set variable <value>``:
  Sets or creates a script variable. The variable may be assigned an arithmetic
  expression, e.g.: ``set xysum x + y``. Supported operators are:
    * ``+``: addition
    * ``-``: subtraction
    * ``*``: multiplication
    * ``/``: division
    * ``%``: modulus (remainder of integer division)

* ``type <value>``:
  Generates key presses and key releases that types the given value.

* ``wait <from-x> <from-y> <to-x> <to-y> <image> [<gone>=0 [<millis>=100]]``:
  Waits for an image within the current screen capture to either appear or
  disappear. ``gone`` is non-zero to wait until the image is no longer there.
  ``millis`` is the delay in milliseconds between checks. The from/to parameters
  limit the area being searched. They work the same way as with ``scan``.

* ``xy x-variable y-variable``:
  Stores the x-y coordinates of the current pointer location to variables.

### <a name="macrec"></a>Macrec

Macrec (Macro recorder) is a utility that prints macro-script lines to STDOUT.
The **bin** directory contains the Macrec launchers:

* **macrec.bat** for Windows
* **macrec** for Unix and GNU/Linux

To record key strokes, start Macrec with the ``-k`` argument:

    macrec -k

This opens a small window that reports key strokes as macro commands to STDOUT.

To record mouse gestures, start Macrec with the ``-m`` argument followed by a
delay in milliseconds, e.g.:

    macrec -m 1000

After the delay, a fullscreen window opens showing a screen capture. This window
have three modes that are activated by pressing the following keys:

* P: Press mouse buttons to records mouse-button presses and releases.
* M: Click in the screen capture to records mouse movement.
* C: Click on two points in the screen capture to select a portion of it. The
  selection is saved as PNG to the current directory.
* S: Click a point to output the coordinates of that point.
* Escape: Exit.

<a name="tinycrumbs"></a>Tiny crumbs
------------------------------------

Various tiny commands. Each has two files in **bin**:

* a **.bat** file for Windows
* a shell script without extension for Unix and GNU/Linux

### <a name="dups"></a>dups

Lists duplicate files.

Usage:

    dups [file/directory] ...

Example:
___

    dups \LotsOfStuff

Sample output:

    \LotsOfStuff\Pix\20150311(1).jpg
    \LotsOfStuff\OldStuff\20150311(1).jpg

    \LotsOfStuff\Paper\draft10.odf
    \LotsOfStuff\Paper\paper.odf
    \LotsOfStuff\Trash\draft10.odf
___

### <a name="openin"></a>openin

Saves STDIN to a temporary file, then opens the temp file with a propgram
specified as a command-line argument.

Usage:

    openin PROGRAM

Example:
___

    dir | openin notepad
___
