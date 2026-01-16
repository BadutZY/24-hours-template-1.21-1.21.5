package com.example.twentyfourhours.config;

import com.example.twentyfourhours.TwentyFourHoursMod;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> createConfigScreen(parent);
    }

    private static Screen createConfigScreen(Screen parent) {
        ModConfig config = ModConfig.getInstance();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.24-hours.title"))
                .setSavingRunnable(() -> {
                    ModConfig.save();
                    // Force sync waktu setelah save config
                    TwentyFourHoursMod.forceSyncTime();
                });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // ============ MAIN SETTINGS CATEGORY ============
        ConfigCategory mainCategory = builder.getOrCreateCategory(
                Text.translatable("config.24-hours.category.main")
        );

        mainCategory.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.translatable("config.24-hours.enabled"),
                        config.enabled
                )
                .setDefaultValue(false)
                .setTooltip(Text.translatable("config.24-hours.enabled.tooltip"))
                .setSaveConsumer(newValue -> config.enabled = newValue)
                .build()
        );

        // Time Mode Selection
        mainCategory.addEntry(entryBuilder
                .startEnumSelector(
                        Text.translatable("config.24-hours.timeMode"),
                        ModConfig.TimeMode.class,
                        config.timeMode
                )
                .setDefaultValue(ModConfig.TimeMode.MANUAL_START_HOUR)
                .setTooltip(Text.translatable("config.24-hours.timeMode.tooltip"))
                .setSaveConsumer(newValue -> config.timeMode = newValue)
                .setEnumNameProvider(value -> {
                    if (value == ModConfig.TimeMode.MANUAL_START_HOUR) {
                        return Text.translatable("config.24-hours.timeMode.manual");
                    } else if (value == ModConfig.TimeMode.COUNTRY_TIMEZONE) {
                        return Text.translatable("config.24-hours.timeMode.country");
                    } else {
                        return Text.of(value.name());
                    }
                })
                .build()
        );

        // ============ MANUAL START HOUR CATEGORY ============
        ConfigCategory manualCategory = builder.getOrCreateCategory(
                Text.translatable("config.24-hours.category.manual")
        );

        manualCategory.addEntry(entryBuilder
                .startTextDescription(
                        Text.translatable("config.24-hours.manual.description")
                )
                .build()
        );

        manualCategory.addEntry(entryBuilder
                .startIntField(
                        Text.translatable("config.24-hours.manualStartHour"),
                        config.manualStartHour
                )
                .setDefaultValue(19)
                .setMin(0)
                .setMax(23)
                .setTooltip(
                        Text.translatable("config.24-hours.manualStartHour.tooltip.line1"),
                        Text.translatable("config.24-hours.manualStartHour.tooltip.line2")
                )
                .setSaveConsumer(newValue -> config.manualStartHour = newValue)
                .build()
        );

        manualCategory.addEntry(entryBuilder
                .startIntField(
                        Text.translatable("config.24-hours.manualStartMinute"),
                        config.manualStartMinute
                )
                .setDefaultValue(0)
                .setMin(0)
                .setMax(59)
                .setTooltip(
                        Text.translatable("config.24-hours.manualStartMinute.tooltip")
                )
                .setSaveConsumer(newValue -> config.manualStartMinute = newValue)
                .build()
        );

        // ============ COUNTRY TIMEZONE CATEGORY ============
        ConfigCategory countryCategory = builder.getOrCreateCategory(
                Text.translatable("config.24-hours.category.country")
        );

        countryCategory.addEntry(entryBuilder
                .startTextDescription(
                        Text.translatable("config.24-hours.country.description")
                )
                .build()
        );

        List<String> countries = Arrays.asList(
                "Indonesia", "United States", "United Kingdom", "Japan", "China",
                "Germany", "France", "Brazil", "Australia", "Canada", "India",
                "Russia", "South Korea", "Mexico", "Spain", "Italy",
                "Netherlands", "Sweden", "Turkey", "Poland"
        );

        countryCategory.addEntry(entryBuilder
                .startStringDropdownMenu(
                        Text.translatable("config.24-hours.selectedCountry"),
                        config.selectedCountry
                )
                .setDefaultValue("Indonesia")
                .setTooltip(Text.translatable("config.24-hours.selectedCountry.tooltip"))
                .setSaveConsumer(newValue -> config.selectedCountry = newValue)
                .setSelections(countries)
                .build()
        );

        // ============ UI SETTINGS CATEGORY ============
        ConfigCategory uiCategory = builder.getOrCreateCategory(
                Text.translatable("config.24-hours.category.ui")
        );

        uiCategory.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.translatable("config.24-hours.showClock"),
                        config.showClock
                )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.24-hours.showClock.tooltip"))
                .setSaveConsumer(newValue -> config.showClock = newValue)
                .build()
        );

        // Clock Format Selection
        uiCategory.addEntry(entryBuilder
                .startEnumSelector(
                        Text.translatable("config.24-hours.clockFormat"),
                        ModConfig.ClockFormat.class,
                        config.clockFormat
                )
                .setDefaultValue(ModConfig.ClockFormat.HOUR_24)
                .setTooltip(Text.translatable("config.24-hours.clockFormat.tooltip"))
                .setSaveConsumer(newValue -> config.clockFormat = newValue)
                .setEnumNameProvider(value -> {
                    if (value == ModConfig.ClockFormat.HOUR_24) {
                        return Text.translatable("config.24-hours.clockFormat.24hour");
                    } else if (value == ModConfig.ClockFormat.HOUR_12) {
                        return Text.translatable("config.24-hours.clockFormat.12hour");
                    } else {
                        return Text.of(value.name());
                    }
                })
                .build()
        );

        uiCategory.addEntry(entryBuilder
                .startIntField(
                        Text.translatable("config.24-hours.clockX"),
                        config.clockX
                )
                .setDefaultValue(10)
                .setTooltip(Text.translatable("config.24-hours.clockX.tooltip"))
                .setSaveConsumer(newValue -> config.clockX = newValue)
                .build()
        );

        uiCategory.addEntry(entryBuilder
                .startIntField(
                        Text.translatable("config.24-hours.clockY"),
                        config.clockY
                )
                .setDefaultValue(10)
                .setTooltip(Text.translatable("config.24-hours.clockY.tooltip"))
                .setSaveConsumer(newValue -> config.clockY = newValue)
                .build()
        );

        uiCategory.addEntry(entryBuilder
                .startAlphaColorField(
                        Text.translatable("config.24-hours.clockColor"),
                        config.clockColor
                )
                .setDefaultValue(0xFFFFFF)
                .setTooltip(Text.translatable("config.24-hours.clockColor.tooltip"))
                .setSaveConsumer(newValue -> config.clockColor = newValue)
                .build()
        );

        uiCategory.addEntry(entryBuilder
                .startFloatField(
                        Text.translatable("config.24-hours.clockScale"),
                        config.clockScale
                )
                .setDefaultValue(1.0f)
                .setMin(0.5f)
                .setMax(3.0f)
                .setTooltip(Text.translatable("config.24-hours.clockScale.tooltip"))
                .setSaveConsumer(newValue -> config.clockScale = newValue)
                .build()
        );

        uiCategory.addEntry(entryBuilder
                .startBooleanToggle(
                        Text.translatable("config.24-hours.showBackground"),
                        config.showBackground
                )
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.24-hours.showBackground.tooltip"))
                .setSaveConsumer(newValue -> config.showBackground = newValue)
                .build()
        );

        uiCategory.addEntry(entryBuilder
                .startAlphaColorField(
                        Text.translatable("config.24-hours.backgroundColor"),
                        config.backgroundColor
                )
                .setDefaultValue(0x80000000)
                .setTooltip(Text.translatable("config.24-hours.backgroundColor.tooltip"))
                .setSaveConsumer(newValue -> config.backgroundColor = newValue)
                .build()
        );

        return builder.build();
    }
}