import {handleFiles} from './hash.js'

$("#file-input").change(async (event) => {
    const algoSelect= document.getElementById("algo-select")
    const algoName = algoSelect.options[algoSelect.selectedIndex].value
    let map = new Map()
    for (let file of event.target.files) {
        const algo = getAlgo(algoName)
        const hash = handleFiles(file, algo, map)
        while (map.get(file.name) == null) {
            await sleep(100)
        }
        console.log("File: ", file, "Hash: ", map.get(file.name))
    }
})

const getAlgo = algoName => {
    switch (algoName.toUpperCase()) {
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

const sleep = ms => {
    return new Promise(resolve => setTimeout(resolve, ms))
}