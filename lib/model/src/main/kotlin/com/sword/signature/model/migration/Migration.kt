package com.sword.signature.model.migration

import com.sword.signature.model.utils.VersionNumberComparator

class Migration(
        val name: String,
        val version: String,
        val content: String
) {
    companion object {
        fun versionComparator() = Comparator<Migration> { migration1, migration2 ->
            VersionNumberComparator.getInstance().compare(migration1.version, migration2.version)
        }
    }
}
