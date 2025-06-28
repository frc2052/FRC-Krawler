package com.team2052.frckrawler.data.local.migration

class MigrationException(
    phase: String,
    message: String,
    cause: Throwable
) : Exception(
    "Failure while migrating legacy database. In phase $phase; $message",
    cause
)