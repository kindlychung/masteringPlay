package models

case class Task(id: Int, name: String)

object Task {

  private var taskList: List[Task] = List()

  def all: List[Task] = {
    taskList
  }

  def add(taskName: String) = {
    val lastId: Int = if (taskList.nonEmpty) taskList.last.id else 0
    taskList = taskList ++ List(Task(lastId + 1, taskName))
  }

  def delete(taskId: Int) = {
    taskList = taskList.filterNot(task => task.id == taskId)
  }
}
