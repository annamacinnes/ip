# Chatty

Chatty is a command-line task management chatbot designed to help you keep track of your tasks efficiently.
You can add tasks, mark them as done, delete them, and search through them using simple text commands.

Chatty supports three types of tasks:

<ul>
   <li>Todo – Simple tasks without date/time</li>
   <li>Deadline – Tasks with a due date</li>
   <li>Event – Tasks with a start and end date</li>
</ul>

## Getting Started
<ol>
   <li>Run the application</li>
   <li>Chatty will greet you with a welcome message</li>
   <li>Enter commands in the input box (or terminal)</li>
   <li>Chatty will respond with confirmation or results</li>
</ol>

## Command Summary
<table>
   <tr>
      <th>Command</th>
      <th>Format</th>
      <th>Example</th>
   </tr>
   <tr>
      <td>List tasks</td>
      <td>list</td>
      <td>list</td>
   </tr>
   <tr>
      <td>Add todo</td>
      <td>todo (description)</td>
      <td>todo read book</td>
   </tr>
   <tr>
      <td>Add deadline</td>
      <td>deadline (description) /by (date)</td>
      <td>deadline submit report /by 2026-03-01</td>
   </tr>
   <tr>
      <td>Add event</td>
      <td>event (description) /from (date) /to (date)</td>
      <td>event project meeting /from 2026-03-01 /to 2026-03-02</td>
   </tr>
   <tr>
      <td>Mark task</td>
      <td>mark (task number)</td>
      <td>mark 2</td>
   </tr>
   <tr>
      <td>Unmark task</td>
      <td>unmark (task number)</td>
      <td>unmark 2</td>
   </tr>
   <tr>
      <td>Delete task</td>
      <td>delete (task number)</td>
      <td>delete 3</td>
   </tr>
   <tr>
      <td>Find tasks</td>
      <td>find (keyword)</td>
      <td>find book</td>
   </tr>
   <tr>
      <td>Tasks due</td>
      <td>due (date)</td>
      <td>due 2026-03-02</td>
   </tr>
</table>

## Features in Detail

### Viewing all tasks

To see all tasks in your list:
```list```

Chatty will display all tasks with their corresponding numbers

### Adding Tasks

#### Add a Todo

```todo <description>```

Example:

```todo revise Java```

#### Add a Deadline

```deadline <description> /by <date>```

Example:

```deadline submit assignment /by 2026-03-10```

#### Add an Event

```event <description> /from <start date> /to <end date>```

Example:

```event hackathon /from 2026-03-01 /to 2026-03-03```

### Marking Tasks as Done

```mark <task number>```

Example:

```mark 1```

Chatty will mark the task as completed.

### Unmarking Tasks

```unmark <task number>```

Example:

```unmark 1```

### Deleting Tasks

```delete <task number>```

Example:

```delete 2```

Chatty will remove the task from your list.

### Finding Tasks

```find <keyword>```

Example:

```find meeting```

Chatty will show all tasks containing the keyword.

### Due Tasks

```due <date>```

Example:

```due 2026-03-03```

Chatty will show all tasks occuring/due on the date.

## Task Numbering

<ul>
   <li>Tasks are numbered starting from 1</li>
   <li>Always use the task number shown in the list command</li>
</ul>

## Error Handling

Chatty will notify you if:
<ul>
   <li>You enter an invalid command</li>
   <li>A task number does not exist</li>
   <li>Required fields (like /by or /from) are missing</li>
   <li>The date format is incorrect</li>
</ul>
Follow the message instructions to correct your input. 

## Data Storage

<ul>
   <li>All tasks are automatically saved to a file.</li>
   <li>When you restart Chatty, your previous tasks will be loaded</li>
</ul>
