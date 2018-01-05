package info.malignantshadow.api.config

class ConfigNumber<out T: Number>(value: T, literal: String) : ConfigValue<T>(value, literal)