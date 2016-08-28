package com.codebutler.farebot.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.codebutler.farebot.BuildConfig;
import com.codebutler.farebot.card.RawCard;
import com.codebutler.farebot.persist.CardPersister;
import com.codebutler.farebot.provider.CardDBHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExportHelper {

    @NonNull private final Context mContext;
    @NonNull private final CardPersister mCardPersister;
    @NonNull private final Gson mGson;

    public ExportHelper(@NonNull Context context, @NonNull CardPersister cardPersister, @NonNull Gson gson) {
        mContext = context;
        mCardPersister = cardPersister;
        mGson = gson;
    }

    @NonNull
    public String exportCards() {
        List<RawCard> cards = new ArrayList<>();
        Cursor cursor = CardDBHelper.createCursor(mContext);
        while (cursor.moveToNext()) {
            cards.add(mCardPersister.readCard(cursor));
        }
        Export export = new Export();
        export.versionName = BuildConfig.VERSION_NAME;
        export.versionCode = BuildConfig.VERSION_CODE;
        export.cards = cards;
        return mGson.toJson(export);
    }

    @NonNull
    public List<Uri> importCards(@NonNull String exportJsonString) {
        List<Uri> uris = new ArrayList<>();
        Export export = mGson.fromJson(exportJsonString, Export.class);
        for (RawCard card : export.cards) {
            uris.add(mCardPersister.saveCard(card));
        }
        return Collections.unmodifiableList(uris);
    }

    @SuppressWarnings({"checkstyle:membername", "checkstyle:visibilitymodifier"})
    private static class Export {
        String versionName;
        int versionCode;
        List<RawCard> cards;
    }
}
