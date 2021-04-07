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

    for (let i = 0; i < data.length; i++) {
        mainContainer.insertAdjacentHTML('afterend', markupCallback(data[i]));
    }
}

function createCommandSummary(command) {
    return `<details>
                <summary>${command.command} - ${command.title}</summary>
                <div>
                    <p>Syntax: <code>/pa ${command.command} ${command.arguments || ''}</code></p>
                    <p>Example: <code>${command.example}</code></p>
                    <p>Permission: <code>${command.permission || 'None required'}</code></p>
                    <p>Console Command: <code>${command.consoleSyntax || 'N/A'}</code></p>
                    <p>Description: ${command.description}</p>
                </div>
            </details>`;
}

function createPlaceholderSummary(placeholderGroup) {
    let placeholderDetails = createPlaceholderDetailsSummary(placeholderGroup);
    return `<h5>${placeholderGroup.heading}</h5>${placeholderGroup.description}${placeholderDetails}`;
}

function createPlaceholderDetailsSummary(placeholderGroup) {
    let result = '';

    for (let i = 0; i < placeholderGroup.placeholders.length; i++) {
        let placeholder = placeholderGroup.placeholders[i];
        result += `<details>
            <summary>${placeholder.placeholder}</summary>
            <div>
                <p>Placeholder: <code>${placeholder.placeholder}</code></p>
                <p>Example output: <code>${placeholder.output}</code></p>
                <p>Description: ${placeholder.description}</p>
            </div>
        </details>`;
    }

    return result;
}
