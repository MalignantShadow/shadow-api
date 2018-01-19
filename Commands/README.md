# ShadowAPI/Commands
This module contains a DSL for creating commands.

## Features
* Easy command building
* The `CommandSource` class can be extended for easy integration into an application, such as a game.
* Optional and required parameters (in any order)
* Commands can be nested, such as `mail read` or `mail write`
* Support for flags:
  * `--flag=VALUE`
  * `--flag`
  * `-f VALUE`
  * `-f` or `-flag`. The latter is parsed as four different flags.
  * When running a command, the flags are processed first, so flag and non-flag arguments
  can be defined in any order: `mail send -m "Welcome aboard" Leonard_McCoy` and
  `mail send Leonard_McCoy -m "Welcome aboard"` work the same.
* If a string is quoted, it is considered a single token. Given the command
`mail send <message>`, then running `mail send "This is my message"` will result in 
the `message` parameter having a value of `This is my message`.
  * This is also true for flags: `--message="This is my message"`
* Optional parameters (even flags) can have default values
* When creating parameters, you can supply a function to define how it should be parsed.
  * The [Util](../Util) module already has a bunch defined in 
  [the ParameterType object](../Util/src/info/malignantshadow/api/util/parsing/ParameterType.kt#L15).
* A good portion of errors are handled already, you only need to worry about errors
specific to your application. Included error handling includes
  * a [flag](src/info/malignantshadow/api/commands/CommandManager.kt#L18)
    or [parameter](src/info/malignantshadow/api/commands/CommandManager.kt#L22)
    with invalid input
  * a [missing flag](src/info/malignantshadow/api/commands/parse/CommandParser.kt#L106)
  * an [exception ocurring](src/info/malignantshadow/api/commands/CommandManager.kt#L133)
   while the command is running
* Simple indication that a command should show help if a flag is present. i.e. `mail --help` or `mail -?`
