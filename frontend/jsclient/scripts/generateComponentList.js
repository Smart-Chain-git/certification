/*
    This script generates the list of all components.

    - It scans recursively all the components in src/ui
    - It create the ts script to import the components.
    - It writes the ts script to src/ui/components.ts.

    For the structure of the file src/ui/components.ts before this script, see
    http://edivgitlab.swordgroup.lan/kami/outside-generique/outside/blob/cc046bef80b9963c5c1b968ff04eb1ebaded6aa2/frontend/src/ui/components.ts
*/

import pth from "path"
import os from "os"
import { promises as fs } from "fs"
import recursive from "recursive-readdir"

// fetchPaths :: () => Promise<String[]>
function fetchPaths() {
  return recursive( "src/ui" )
}

// isVueComponent :: String => Boolean
function isVueComponent(path) {
  return pth.extname( path ) === ".vue"
}

// getComponentName :: String => String
function getComponentName(path) {
  return pth.basename( path, ".vue" )
}

// getImportPath :: String => String
function getImportPath(path) {
  const normalizedPath = path.split( pth.sep ).join( "/" )
  return "@" + normalizedPath.substring( 3 ) // removes 'src' at beginning of path
}

// formatPathToLine :: String => String
function formatPathToLine(path) {
  const componentName = getComponentName( path )
  const importPath = getImportPath( path )
  return `export { default as ${componentName} } from "${importPath}"`
}

// formatLinesToScript :: String[] => String
function formatLinesToScript(lines) {
  const warning1 = "// AUTO GENERATED : DO NOT MODIFY"
  const warning2 = "// See scripts/generateComponentList.js"
  const tslint = "/* tslint:disable:max-line-length */"
  const allLines = [warning1, warning2, "", tslint, ""].concat( lines, [''] )
  return allLines.join( os.EOL )
}

// writeScriptToFile :: String => Promise<void>
function writeScriptToFile(script) {
  return fs.writeFile( "build/generated/src/ui/components.ts", script )
}

fs.mkdir( "build/generated/src/ui", { recursive: true } )
    .catch( () => {/*pas grave car existe deja*/} )
    .then( () => fetchPaths() )
    .then( paths => paths.filter( isVueComponent ) )
    .then( paths => paths.map( formatPathToLine ) )
    .then( lines => formatLinesToScript( lines ) )
    .then( script => writeScriptToFile( script ) )

