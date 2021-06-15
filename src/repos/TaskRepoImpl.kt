package dev.hashnode.danielwaiguru.repos

import dev.hashnode.danielwaiguru.database.DatabaseFactory.dbQuery
import dev.hashnode.danielwaiguru.database.Tasks
import dev.hashnode.danielwaiguru.models.Task
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

class TaskRepoImpl: TaskRepo {
    override suspend fun storeTask(uid: Int, title: String, description: String, done: Boolean): Task? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Tasks.insert { task ->
                task[Tasks.uid] = uid
                task[Tasks.title] = title
                task[Tasks.description] = description
                task[Tasks.done] = done
            }
        }
        return rowToTask(statement?.resultedValues?.get(0))
    }

    override suspend fun getTasks(uid: Int): List<Task> = dbQuery {
        Tasks.select { Tasks.uid.eq(uid) }.mapNotNull { rowToTask(it) }
    }
    private fun rowToTask(row: ResultRow?): Task? {
        if (row == null) {
            return null
        }
        return Task(
            taskId = row[Tasks.taskId],
            uid = row[Tasks.uid],
            title = row[Tasks.title],
            description = row[Tasks.description],
            done = row[Tasks.done]
        )
    }
}