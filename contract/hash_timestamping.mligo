type record_hash = string
(* The information stored are the timestamp of writing and the address of the writer. *)
type record = timestamp * address
(* We store a big map containing a record hash as key and the corresponding info as value. *)
type storage = (record_hash, record) big_map
type return = operation list * storage

let addHash (new_hash, store: record_hash * storage) : return =
(* We check if the hash is already written in the map. *)
  match (Big_map.find_opt new_hash store) with
  (* If found we fail due to uniqueness constraint. *)
  | Some d -> (failwith "Record hash already written" : return)
  (* Else we retrieve the timestamp and writer and save it into the map. *)
  | None ->
    let current = Current.time
    in
    let result = Big_map.add new_hash (current, sender) store
    in
    (([]: operation list), result)
