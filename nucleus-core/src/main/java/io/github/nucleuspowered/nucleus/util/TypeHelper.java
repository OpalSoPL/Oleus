/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.util.Tuple;

import java.util.Arrays;
import java.util.List;

public final class TypeHelper {

    private TypeHelper() {}

    public static Tuple<DataQuery, List<?>> getList(final DataQuery query, final Object array) {

        if (array instanceof byte[]) {
            return Tuple.of(getNewName(query, "B"), Arrays.asList(ArrayUtils.toObject((byte[]) array)));
        } else if (array instanceof short[]) {
            return Tuple.of(getNewName(query, "S"), Arrays.asList(ArrayUtils.toObject((short[]) array)));
        } else if (array instanceof int[]) {
            return Tuple.of(getNewName(query, "I"), Arrays.asList(ArrayUtils.toObject((int[]) array)));
        } else if (array instanceof long[]) {
            return Tuple.of(getNewName(query, "J"), Arrays.asList(ArrayUtils.toObject((long[]) array)));
        } else if (array instanceof float[]) {
            return Tuple.of(getNewName(query, "F"), Arrays.asList(ArrayUtils.toObject((float[]) array)));
        } else if (array instanceof double[]) {
            return Tuple.of(getNewName(query, "D"), Arrays.asList(ArrayUtils.toObject((double[]) array)));
        } else if (array instanceof boolean[]) {
            return Tuple.of(getNewName(query, "Z"), Arrays.asList(ArrayUtils.toObject((boolean[]) array)));
        }

        throw new RuntimeException();
    }

    public static Tuple<DataQuery, Object> getArray(final DataQuery query, final DataView container) {
        final String a = query.asString(".");
        final DataQuery q = DataQuery.of('.', query.asString(".").replaceAll("\\$Array\\$[a-zA-Z]$", ""));
        final String objType = a.substring(a.length() - 1);
        final List<?> array = container.getList(query).orElse(Lists.newArrayList());
        final int size = array.size();

        switch (objType) {
            case "B": {
                final byte[] b = new byte[size];
                for (int i = 0; i < size; i++) {
                    final Object obj = array.get(i);
                    if (obj instanceof String) {
                        b[i] = Byte.parseByte((String) obj);
                    } else {
                        b[i] = ((Number) obj).byteValue();
                    }
                }

                return Tuple.of(q, b);
            }
            case "S": {
                final short[] b = new short[size];
                for (int i = 0; i < size; i++) {
                    final Object obj = array.get(i);
                    if (obj instanceof String) {
                        b[i] = Short.parseShort((String) obj);
                    } else {
                        b[i] = ((Number) obj).shortValue();
                    }
                }

                return Tuple.of(q, b);
            }
            case "I": {
                final int[] b = new int[size];
                for (int i = 0; i < size; i++) {
                    final Object obj = array.get(i);
                    if (obj instanceof String) {
                        b[i] = Integer.parseInt((String) obj);
                    } else {
                        b[i] = ((Number) obj).intValue();
                    }
                }

                return Tuple.of(q, b);
            }
            case "J": {
                final long[] b = new long[size];
                for (int i = 0; i < size; i++) {
                    final Object obj = array.get(i);
                    if (obj instanceof String) {
                        b[i] = Long.parseLong((String) obj);
                    } else {
                        b[i] = ((Number) obj).longValue();
                    }
                }

                return Tuple.of(q, b);
            }
            case "F": {
                final float[] b = new float[size];
                for (int i = 0; i < size; i++) {
                    final Object obj = array.get(i);
                    if (obj instanceof String) {
                        b[i] = Float.parseFloat((String) obj);
                    } else {
                        b[i] = ((Number) obj).floatValue();
                    }
                }

                return Tuple.of(q, b);
            }
            case "D": {
                final double[] b = new double[size];
                for (int i = 0; i < size; i++) {
                    final Object obj = array.get(i);
                    if (obj instanceof String) {
                        b[i] = Double.parseDouble((String) obj);
                    } else {
                        b[i] = ((Number) obj).doubleValue();
                    }
                }

                return Tuple.of(q, b);
            }
            case "Z": {
                final boolean[] b = new boolean[size];
                for (int i = 0; i < size; i++) {
                    final Object obj = array.get(i);
                    if (obj instanceof String) {
                        b[i] = Boolean.parseBoolean((String) obj);
                    } else {
                        b[i] = (Boolean) obj;
                    }
                }

                return Tuple.of(q, b);
            }
        }

        throw new RuntimeException();
    }

    private static DataQuery getNewName(final DataQuery dataQuery, final String name) {
        return DataQuery.of('.', dataQuery.asString(".") + "$Array$" + name);
    }


}
