/*
Copyright 2017 Olga Miller <olga.rgb@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package om.sstvencoder;

import android.content.Context;
import android.net.Uri;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import om.sstvencoder.Modes.ModeFactory;

class Settings {
    private final static String VERSION = "version";
    private final static String IMAGE_URI = "image_uri";
    private final static String TEXT_OVERLAY_PATH = "text_overlay_path";
    private final static String MODE_CLASS_NAME = "mode_class_name";
    private final int mVersion;
    private final String mFileName;
    private Context mContext;
    private String mModeClassName;
    private String mImageUri;
    private String mTextOverlayPath;

    private Settings() {
        mVersion = 1;
        mFileName = "settings.json";
        mModeClassName = ModeFactory.getDefaultModeClassName();
    }

    Settings(Context context) {
        this();
        mContext = context;
    }

    boolean load() {
        boolean loaded = false;
        JsonReader reader = null;
        try {
            InputStream in = new FileInputStream(getFile());
            reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            read(reader);
            loaded = true;
        } catch (Exception ignore) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignore) {
                }
            }
        }
        return loaded;
    }

    boolean save() {
        boolean saved = false;
        JsonWriter writer = null;
        try {
            OutputStream out = new FileOutputStream(getFile());
            writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.setIndent(" ");
            write(writer);
            saved = true;
        } catch (Exception ignore) {
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ignore) {
                }
            }
        }
        return saved;
    }

    void setModeClassName(String modeClassName) {
        mModeClassName = modeClassName;
    }

    String getModeClassName() {
        return mModeClassName;
    }

    void setImageUri(Uri uri) {
        mImageUri = uri == null ? null : uri.toString();
    }

    Uri getImageUri() {
        if (mImageUri == null)
            return null;
        return Uri.parse(mImageUri);
    }

    File getTextOverlayFile() {
        if (mTextOverlayPath == null)
            mTextOverlayPath = new File(mContext.getFilesDir(), "text_overlay.json").getPath();
        return new File(mTextOverlayPath);
    }

    private File getFile() {
        return new File(mContext.getFilesDir(), mFileName);
    }

    private void write(JsonWriter writer) throws IOException {
        writer.beginObject();
        {
            writeVersion(writer);
            writeModeClassName(writer);
            writeImageUri(writer);
            writeTextOverlayPath(writer);
        }
        writer.endObject();
    }

    private void writeVersion(JsonWriter writer) throws IOException {
        writer.name(VERSION).value(mVersion);
    }

    private void writeModeClassName(JsonWriter writer) throws IOException {
        writer.name(MODE_CLASS_NAME).value(mModeClassName);
    }

    private void writeImageUri(JsonWriter writer) throws IOException {
        writer.name(IMAGE_URI).value(mImageUri);
    }

    private void writeTextOverlayPath(JsonWriter writer) throws IOException {
        writer.name(TEXT_OVERLAY_PATH).value(mTextOverlayPath);
    }

    private void read(JsonReader reader) throws IOException {
        reader.beginObject();
        {
            if (readVersion(reader) == mVersion) {
                readModeClassName(reader);
                readImageUri(reader);
                readTextOverlayPath(reader);
            }
        }
        reader.endObject();
    }

    private int readVersion(JsonReader reader) throws IOException {
        reader.nextName();
        return reader.nextInt();
    }

    private void readModeClassName(JsonReader reader) throws IOException {
        reader.nextName();
        mModeClassName = reader.nextString();
    }

    private void readImageUri(JsonReader reader) throws IOException {
        reader.nextName();
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            mImageUri = null;
        } else
            mImageUri = reader.nextString();
    }

    private void readTextOverlayPath(JsonReader reader) throws IOException {
        reader.nextName();
        mTextOverlayPath = reader.nextString();
    }
}
