/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.internal.messages;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class MessageProvider {

    public abstract Optional<String> getMessageFromKey(String key);
    private final Map<String, TextTemplate> textTemplateMap = Maps.newHashMap();

    public String getMessageWithFormat(String key, String... substitutions) {
        try {
            return MessageFormat.format(getMessageFromKey(key).get(), (Object[]) substitutions);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("The message key " + key + " does not exist!");
        }
    }

    public final Text getTextMessageWithFormat(String key, String... substitutions) {
        return getTextMessageWithTextFormat(key, Arrays.stream(substitutions).map(Text::of).collect(Collectors.toList()));
    }

    public final Text getTextMessageWithTextFormat(String key, Text... substitutions) {
        return getTextMessageWithTextFormat(key, Arrays.asList(substitutions));
    }

    public final Text getTextMessageWithTextFormat(String key, List<Text> textList) {
        TextTemplate template = textTemplateMap.computeIfAbsent(key, k -> templateCreator(getMessageWithFormat(k)));
        return template.apply(textList.stream().collect(Collectors.toMap(k -> String.valueOf(textList.indexOf(k)), v -> v))).build();
    }

    private TextTemplate templateCreator(String string) {
        // regex!
        Matcher mat = Pattern.compile("\\{([\\d]+)}").matcher(string);
        List<Integer> map = Lists.newArrayList();

        while (mat.find()) {
            map.add(Integer.parseInt(mat.group(1)));
        }

        String[] s = string.split("\\{([\\d]+)}");

        List<Object> objects = Lists.newArrayList();
        objects.add(TextSerializers.FORMATTING_CODE.deserialize(s[0]));
        map.forEach(x -> {
            objects.add(TextTemplate.arg(x.toString()));
            if (s.length > x + 1) {
                objects.add(TextSerializers.FORMATTING_CODE.deserialize(s[x + 1]));
            }
        });

        return TextTemplate.of((Object[])objects.toArray(new Object[objects.size()]));
    }
}
