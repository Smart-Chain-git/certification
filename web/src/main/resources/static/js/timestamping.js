import {handleFiles} from './hash.js'

const getAlgorithm = algorithmName => {
    switch (algorithmName.toUpperCase()) {
        case "MD5":
            return CryptoJS.algo.MD5.create()
        case "SHA-1":
            return CryptoJS.algo.SHA1.create()
        case "SHA-256":
            return CryptoJS.algo.SHA256.create()
        case "SHA-384":
            return CryptoJS.algo.SHA384.create()
        case "SHA-512":
            return CryptoJS.algo.SHA512.create()
        default:
            throw Error("Unknown algorithm.")
    }
}

function uniqueID() {
    function chr4() {
        return Math.random().toString(16).slice(-4)
    }

    return chr4() + chr4() +
        '-' + chr4() +
        '-' + chr4() +
        '-' + chr4() +
        '-' + chr4() + chr4() + chr4()
}


$("#file-input").change(async (event) => {
    const algorithmSelect = document.getElementById("algo-select")
    const algorithmName = algorithmSelect.options[algorithmSelect.selectedIndex].value
    for (let file of event.target.files) {
        const algorithm = getAlgorithm(algorithmName)
        const fileId = initFileDisplay(file)
        handleFiles(file, algorithm, hash => updateFileHash(fileId, hash))
    }
})

const resetFileDisplay = () => {
    $("#file-info-body").empty()
}

const initFileDisplay = file => {
    const fileId = uniqueID()
    $("#file-info-body").append(
        '<tr id=\"file-' + fileId + '\">' +
        '<td><input name="file-name" value=\"' + file.name + '\" readonly></td>' +
        '<td><input name="file-size" value=\"' + file.size + '\" readonly></td>' +
        '<td id=\"hash-' + fileId + '\"><div class=\"spinner-border text-primary\"></div></td>' +
        '</tr>')
    return fileId
}

const updateFileHash = (fileId, hash) => {
    document.getElementById("hash-" + fileId).innerHTML = '<input name ="file-hash" value=\"' + hash + '\" readonly>'
}

export const readyToUpload = () => {
    console.log("Checking readiness")
    $('file-info-body').children().forEach(file => {
            console.log(file)
        }
    )
    return true
}