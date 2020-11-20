/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.note;

import com.google.common.reflect.TypeToken;
import io.github.nucleuspowered.nucleus.modules.note.data.NoteData;
import io.github.nucleuspowered.nucleus.services.impl.storage.dataobjects.modular.IUserDataObject;
import io.github.nucleuspowered.storage.dataobjects.keyed.DataKey;

public final class NoteKeys {

    public static final TypeToken<NoteData> NOTE_DATA_KEY = TypeToken.of(NoteData.class);

    public final static DataKey.ListKey<NoteData, IUserDataObject> NOTE_DATA =
            DataKey.ofList(NoteKeys.NOTE_DATA_KEY, IUserDataObject.class, "notes");
}
