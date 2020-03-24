package com.sword.signature.model.migration

open class MigrationException : RuntimeException {
    constructor(message: String) : super(message)
}

class MissingAppliedMigrationException(migrationName: String) : MigrationException("The applied migration '$migrationName' is missing in the migration folder.")
class WrongMigrationOrderException(migrationName: String) : MigrationException("The migration '$migrationName' cannot be applied before applied migration.")
class WrongMigrationChecksumException(migrationName: String, appliedChecksum: String, newChecksum: String) :
        MigrationException("The migration '$migrationName' checksums do not match. Applied: '$appliedChecksum'; found: '$newChecksum'.")