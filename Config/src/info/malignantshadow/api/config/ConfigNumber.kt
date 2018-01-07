package info.malignantshadow.api.config

/**
 * Represents a Number that can be represented as a String
 * @author Shad0w (Caleb Downs)
 */
class ConfigNumber<out T : Number>(value: T, literal: String) : ConfigValue<T>(value, literal)