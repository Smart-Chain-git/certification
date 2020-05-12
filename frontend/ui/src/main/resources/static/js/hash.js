const chunkSize = 1024 * 1024 // 1MB
const timeout = 10 // 10ms

export function handleFiles(file, algo, applyResult) {
    let counter = 0
    let lastOffset = 0
    let chunkReorder = 0
    let chunkTotal = 0
    let previous = []

    function loading(file, callbackProgress, callbackFinal) {
        let offset = 0
        let size = chunkSize
        let partial
        let index = 0

        if (file.size === 0) {
            callbackFinal()
        }
        while (offset < file.size) {
            partial = file.slice(offset, offset + size)
            let reader = new FileReader
            reader.size = chunkSize
            reader.offset = offset
            reader.index = index
            reader.onload = function (evt) {
                callbackRead(this, file, evt, callbackProgress, callbackFinal)
            }
            reader.readAsArrayBuffer(partial)
            offset += chunkSize
            index += 1
        }
    }

    // memory reordering
    function callbackRead(reader, file, evt, callbackProgress, callbackFinal) {
        chunkTotal++

        if (lastOffset !== reader.offset) {
            // out of order
            //console.log("[", reader.size, "]", reader.offset, '->', reader.offset + reader.size, ">>buffer")
            previous.push({offset: reader.offset, size: reader.size, result: reader.result})
            chunkReorder++
            return
        }

        function parseResult(offset, size, result) {
            lastOffset = offset + size
            callbackProgress(result)
            if (offset + size >= file.size) {
                lastOffset = 0
                callbackFinal()
            }
        }

        // in order
        //console.log("[", reader.size, "]", reader.offset, '->', reader.offset + reader.size, "")
        parseResult(reader.offset, reader.size, reader.result)

        // resolve previous buffered
        let buffered = [{}]
        while (buffered.length > 0) {
            buffered = previous.filter(function (item) {
                return item.offset === lastOffset
            })
            buffered.forEach(function (item) {
                // console.log("[", item.size, "]", item.offset, '->', item.offset + item.size, "<<buffer")
                parseResult(item.offset, item.size, item.result)
                previous.remove(item)
            })
        }

    }

    loading(file,
        function (data) {
            let wordBuffer = CryptoJS.lib.WordArray.create(data)
            algo.update(wordBuffer)
            counter += data.byteLength
        }, function (data) {
            let encrypted = algo.finalize().toString()
            applyResult(encrypted)
        })

}

Array.prototype.remove = Array.prototype.remove || function (val) {
    let i = this.length
    while (i--) {
        if (this[i] === val) {
            this.splice(i, 1)
        }
    }
}