
package com.android.mobsec;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for PolElemProvider
 */
public final class policyElem {
    public static final String AUTHORITY = "com.google.provider.MobSec";

    // This class cannot be instantiated
    private policyElem() {}
    
    /**
     * Notes table
     */
    public static final class Elements implements BaseColumns {
        // This class cannot be instantiated
        private Elements() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/elements");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.element";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.element";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        /**
         * The name of the policy
         * <P>Type: TEXT</P>
         */
        public static final String NAME = "name";

        /**
         * The IP address
         * <P>Type: TEXT</P>
         */
        public static final String IPADDR = "ipAddr";
        
        /**
         * The network mask
         * <P>Type: TEXT</P>
         */
        public static final String NETMASK = "netMask";

        /**
         * The timestamp for when the note was created
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String CREATED_DATE = "created";

        /**
         * The timestamp for when the note was last modified
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String MODIFIED_DATE = "modified";
    }
}
