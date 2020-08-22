/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.quickstart;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import uk.co.drnaylor.quickstart.config.TypedAbstractConfigAdapter;

public abstract class NucleusConfigAdapter<R> extends TypedAbstractConfigAdapter<R> {

    public abstract static class Standard<R> extends NucleusConfigAdapter<R> {

        final TypeToken<R> typeToken;
        final Class<R> clazz;

        public Standard(final Class<R> clazz) {
            this(TypeToken.of(clazz));
        }

        public Standard(final TypeToken<R> typeToken) {
            this.typeToken = typeToken;
            this.clazz = (Class<R>) typeToken.getRawType();
        }

        public Class<R> getConfigClass() {
            return this.clazz;
        }

        @Override
        protected R convertFromConfigurateNode(final ConfigurationNode node) throws ObjectMappingException {
            return node.getValue(this.typeToken, this.getDefaultObject());
        }

        @Override
        protected ConfigurationNode insertIntoConfigurateNode(final ConfigurationNode newNode, final R data) throws ObjectMappingException {
            return newNode.setValue(this.typeToken, data);
        }
    }

    public abstract static class StandardWithSimpleDefault<R> extends NucleusConfigAdapter.Standard<R> {

        public StandardWithSimpleDefault(final Class<R> clazz) {
            super(clazz);
        }

        public StandardWithSimpleDefault(final TypeToken<R> typeToken) {
            super(typeToken);
        }

        @Override
        @SuppressWarnings("unchecked")
        public R getDefaultObject() {
            try {
                return (R) this.typeToken.getRawType().newInstance();
            } catch (final InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
