const group = [];
group[1] = 'Basic'
group[2] = 'Create'
group[3] = 'Course'
group[4] = 'Player'
group[5] = 'Admin'

function insertCommandsMarkup() {
    fetch('files/parkourCommands.json')
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            appendData(data, 'parkour-commands', createCommandSummary);
        })
        .catch(function (err) {
            console.log(err);
        });
}

function insertPlaceholdersMarkup() {
    fetch('files/parkourPlaceholders.json')
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            appendData(data, 'parkour-placeholders', createPlaceholderSummary);
        })
        .catch(function (err) {
            console.log(err);
        });
}

function appendData(data, elementId, markupCallback) {
    data = data.reverse();
    let mainContainer = document.getElementById(elementId);

    for (const datum of data) {
        mainContainer.insertAdjacentHTML('afterend', markupCallback(datum));
    }
}

function createCommandSummary(command) {
    const yoyo = group[command.commandGroup];
    const styleClass = command.deprecated ? 'deprecated' : '';
    let result = `<details>
                    <summary class="${styleClass}">
                        <strong>${command.command}</strong> - ${command.title} <em>${yoyo}</em>
                    </summary>
                    <div>
                        <table>
                            <tbody>`;

    if (command.deprecated) {
        result += `
                                <tr>
                                    <th scope="row"><strong>Deprecated</strong></th>
                                    <td>Instead use: <code>${command.deprecated}</code></td>
                                </tr>`
    }

    if (!command.consoleOnly) {
        result += `
                                <tr>
                                    <th scope="row">Syntax</th>
                                    <td><code>/pa ${command.command} ${command.arguments || ''}</code></td>
                                </tr>
                                <tr>
                                    <th scope="row">Example</th>
                                    <td><code>${command.example}</code></td>
                                </tr>
                                <tr>
                                    <th scope="row">Permission</th>
                                    <td><code>${command.permission || 'None required'}</code></td>
                                </tr>`
        }
        result += `
                                <tr>
                                    <th scope="row">Console Command</th>
                                    <td><code>${command.consoleSyntax || 'N/A'}</code></td>
                                </tr>
                                <tr>
                                    <th scope="row">Description</th>
                                    <td>${command.description}</td>
                                </tr>
                            </tbody>
                        </table>
                     </div>
                 </details>`;
    return result;
}


function createPlaceholderSummary(placeholderGroup) {
    let placeholderDetails = createPlaceholderDetailsSummary(placeholderGroup);
    return `<h5>${placeholderGroup.heading}</h5>${placeholderGroup.description}${placeholderDetails}`;
}

function createPlaceholderDetailsSummary(placeholderGroup) {
    let result = '';
    for (const placeholder of placeholderGroup.placeholders) {
        result += `<details>
            <summary><strong>${placeholder.placeholder}</strong></summary>
            <div>
                <table>
                <tbody>
                    <tr>
                        <th scope="row">Placeholder</th>
                        <td><code>${placeholder.placeholder}</code></td>
                    </tr>
                    <tr>
                        <th scope="row">Example output</th>
                        <td><code>${placeholder.output}</code></td>
                    </tr>
                    <tr>
                        <th scope="row">Description</th>
                        <td>${placeholder.description}</td>
                    </tr>
                </tbody>
                </table>
            </div>
        </details>`;
    }

    return result;
}
