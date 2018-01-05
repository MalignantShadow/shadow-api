(Javadoc unfinished/unpublished)

#ShadowAPI/Config

This module provides a means to process configuration files into a single, simple format. Most popular configuration
types support key/value pairs, arrays, and maps.

In this module, arrays represented as `ConfigSequence`, they can hold any number of (nullable) values.

Maps are represented as `ConfigSection`, they can hold any number of pairs, but no two pairs can have the same key.
Keys are non-null `String`s that can be empty

# JSON
JSON configuration processing is available via the
[JsonConfigProcessor](src/info/malignantshadow/api/config/processor/types/JsonConfigProcessor.kt) class.

# YAML
Similarly, YAML configuration processing is available via the
[YamlConfigProcessor](src/info/malignantshadow/api/config/processor/types/YamlConfigProcessor.kt) class. It uses
[SnakeYAML](//bitbucket.org/asomov/snakeyaml) as a backend

# Shade
Shade is a config  format that I've designed. It's similar in syntax to JSON, with the following exceptions:
* Line comments are supported with `//`, `;` and `#`
* Block comments are supported with `### ... ###` and `/* ... */`
* `,` is not used to separate values in lists or key/value pairs in maps
* `:` is unused. Spaces separate keys and values.
* Like YAML, keys do not need quotes unless they would otherwise be mistaken as something else, like a number.
* The keywords `on` and `yes` can be used instead of `true`, and will be preserved if not overwritten
* The keywords `off` and `no` can be used instead of `false`, and will be preserved if not overwritten
* Strings can be represent with single-quotes as well as double-quotes
* Multiple keys can be separated with a comma (`,`) for a specific value. Note that each key has a value that is
  *structurally* identical (`==`) but not actually the same object (`===`)
* The closing characters `]` (for arrays) and `}` (for maps) are optional if they are the last item.
  * Internally, this is handled by simply returning the map/array when the end of the source string has been reached
  (at an appropriate time)
  * This design choice was due the fact that, sometimes, JSON files are clustered with `]` and `{` at the end, which
    (in my opinion) is unsightly.

Processing for Shade is handled via the
[ShadeConfigProcessor](src/info/malignantshadow/api/config/processor/types/ShadeConfigProcessor.kt) class

## Why's It Called 'Shade?' ... Weirdo
Configuration systems aren't really named after their purpose (usually). The name is a reference to my screen name,
MalignantShadow (or sometimes Shad0w), as well as a reference to another project that I'm keeping secret for now.

## What does it look like?
Good question! Here's an example, which is basically a `package.json` converted to Shade

```plaintext
name "my-awesome-project"
version "1.0.0"
dependencies {
  react "^15.0.0"
  prop-types "^15.6.0"
  material-ui "^1.0.0-beta.20"
  material-ui-icons "^1.0.0-beta.15"
  typeface-roboto "0.0.35"
}
```

Or slightly prettier:

```plaintext
name                "my-awesome-project"
version             "1.0.0"
dependencies {
  react             "^15.0.0"
  prop-types        "^15.6.0"
  material-ui       "^1.0.0-beta.20"
  material-ui-icons "^1.0.0-beta.15"
  typeface-roboto   "0.0.35"
```
(Note how the last `}` is missing, this is perfectly fine, because 'dependencies' is the last item)

Here's a more complex example:

```plaintext
fireteam {
  name "Intrepid"
  members [
    {
      name "Iris"
      race: "ai"
    }
    {
      name "Scarlett Asher"
      race "human"
      kingdom "helix"
      classification "salamander"
      weapon {
        name "?unnamed"
        types [
          "shotgun"
          "spear"
        ]
      }
    }
  ]
}
```
Let's make this a bit prettier by adding some whitespace and removing several characters:
```plaintext
fireteam {
  name "Intrepid"
  members [
    {
      name           "Iris"
      race           "ai"
    } {
      name           "Scarlett Asher"
      race           "human"
      kingdom        "helix"
      classification "salamander"
      weapon {
        name         "?unnamed"
        types [
          "shotgun" "spear"
```
It looks a little weird, but the parser still accepts it as valid data