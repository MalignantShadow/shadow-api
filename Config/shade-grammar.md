# Shade Grammar

## Identifiers
An identifier is essentially a string, except that only a small set of characters is allowed, and they are unquoted.
* RegEx used - `[a-zA-Z_-][\w_\-]*`
* English: A word that starts with an alphabetic character, underscore, or dash, followed by any amount of alphanumeric
  characters, underscores, or dashes
* Examples:
  * `camelCase`
  * `CapitalCase`
  * `-i-love-dashes-`
  * `underscores_are_cool_too`
  * etc.

## Strings
A string is basically what you expect from a normal programming language, except that both single-quoting and
double-quoting is allowed, and the only escape sequences allowed are `\n`, `\r`, `\f`, `\t`, `\"`, and `\'`.
* RegEx used - `(["'])([^"'\n\r\f\t]|\\["'nrft])\2` (`\2` is used here because all supplied RegExes
  to a `Tokenizer` are wrapped in `^($regex)`, so all back-references start with 2)
* English: A quote character (`quote`) followed by either: a character a that isn't `"`, `'`, a newline or tab character;
  or an escape character `\` followed by `"`, `'`, `n`, `r`, `f`, or `t`; followed by `quote`

## Null
`null` is used to represent the presence of one or more keys without a value

## Booleans
Aside from the normal `true` and `false` literals, Shade understands a few more:
* `on`, `yes` - Evaluates to `true`
* `off`, `no` - Evaluates to `false`

## Integers
Integers are parsed via a simple RegEx: `[+-]?\d+`. In English, this means an optional sign character followed by one or
more digits (0-9)

## Floating Point Numbers (java/kotlin Doubles)
The RegEx used to determine a floating point number is not as complex as Javas:
`[+-]?\d*(\.\d+([eE][+-]?\d+)?)\b`. This matches:
  * A decimal less than 0 - `.1234`
  * A number on both sides of the decimal - `3.14`
  * Scientific Notation via 'e' or 'E' - `1.2345E15`
  * etc.

## Maps
Maps start with `{` and end with `}` or the end of the source string. They contain either one or more keys (which can
be a string or an identifier, followed by `,`) followed by a value.

A comma denotes that another key should be read, so the following is invalid:
```plaintext
key, {
  value "string"
}
```
The tokenizer will say that it expected a `STRING` or `IDENTIFIER` but found '{'

## Lists
Lists start with `[` and end with `]` or the end of the source string. They contain space-separated values.
(More formally, space separated tokens)