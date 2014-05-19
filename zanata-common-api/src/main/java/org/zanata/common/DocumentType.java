package org.zanata.common;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

public enum DocumentType {
    GETTEXT_PORTABLE_OBJECT("po"), GETTEXT_PORTABLE_OBJECT_TEMPLATE("pot"),
    PLAIN_TEXT("txt"), XML_DOCUMENT_TYPE_DEFINITION("dtd"),

    OPEN_DOCUMENT_TEXT("odt"), OPEN_DOCUMENT_TEXT_FLAT("fodt"),
    OPEN_DOCUMENT_PRESENTATION("odp"), OPEN_DOCUMENT_PRESENTATION_FLAT("fodp"),
    OPEN_DOCUMENT_SPREADSHEET("ods"), OPEN_DOCUMENT_SPREADSHEET_FLAT("fods"),
    OPEN_DOCUMENT_GRAPHICS("odg"), OPEN_DOCUMENT_GRAPHICS_FLAT("fodg"),
    OPEN_DOCUMENT_DATABASE("odb"), OPEN_DOCUMENT_FORMULA("odf"),

    HTML("html", "htm"), IDML("idml"),

    SUBTITLE("srt", "sbt", "sub", "vtt");

    private static final List<String> allExtensions = buildExtensionsList();

    private static List<String> buildExtensionsList() {
        List<String> allExtensions = new ArrayList<String>();
        for (DocumentType type : DocumentType.values()) {
            allExtensions.addAll(type.extensions);
        }
        return unmodifiableList(allExtensions);
    }

    /**
     * @return a read-only list of file extensions for known file types
     */
    public static List<String> getAllExtensions() {
        return allExtensions;
    }

    // FIXME damason: rename typeFor to fromString
    public static DocumentType typeFor(String extension) {
        for (DocumentType type : DocumentType.values()) {
            if (type.extensions.contains(extension)) {
                return type;
            }
        }
        // FIXME damason: throw new IllegalArgumentException
        return null;
    }

    private final List<String> extensions;

    /**
     * Create a document type enum constant with the given list of extensions.
     * At least one extension must be specified.
     *
     * @throws IllegalArgumentException
     *             if no extensions are specified
     */
    DocumentType(@Nonnull String... extensions) throws IllegalArgumentException {
        if (extensions.length == 0) {
            throw new IllegalArgumentException(
                    "DocumentType must be constructed with at least one extension.");
        }
        this.extensions = unmodifiableList(Arrays.asList(extensions));
    }

    // FIXME damason: rename getExtension to toString
    @Deprecated
    public String getExtension() {
        return extensions.get(0);
    }

    public List<String> getExtensions() {
        return extensions;
    }
}