;;1. read file and hold it in memory
(slurp "some.txt")

;;2. read file line by line
(with-open [rdr (java.io.BufferedReader. 
                 (java.io.FileReader. "project.clj"))]
  (let [seq (line-seq rdr)]
    (count seq)))

;;3. read from stream
(defn fetch-url[address]
  (with-open [stream (.openStream (java.net.URL. address))]
    (let  [buf (java.io.BufferedReader. 
                (java.io.InputStreamReader. stream))]
      (apply str (line-seq buf)))))

(fetch-url "http://google.com")

(defn fetch-data [url]
  (let  [con    (-> url java.net.URL. .openConnection)
         fields (reduce (fn [h v] 
                          (assoc h (.getKey v) (into [] (.getValue v))))
                        {} (.getHeaderFields con))
         size   (first (fields "Content-Length"))
         in     (java.io.BufferedInputStream. (.getInputStream con))
         out    (java.io.BufferedOutputStream. 
                 (java.io.FileOutputStream. "out.file"))
         buffer (make-array Byte/TYPE 1024)]
    (loop [g (.read in buffer)
           r 0]
      (if-not (= g -1)
        (do
          (println r "/" size)
          (.write out buffer 0 g)
          (recur (.read in buffer) (+ r g)))))
    (.close in)
    (.close out)
    (.disconnect con)))

(fetch-data "http://google.com")

;;4. read from socket
(defn socket [host port]
  (let [socket (java.net.Socket. host port)
        in (java.io.BufferedReader. 
            (java.io.InputStreamReader. (.getInputStream socket)))
        out (java.io.PrintWriter. (.getOutputStream socket))]
    {:in in :out out}))

(def conn (socket "irc.freenode.net" 6667))
(println (.readLine (:in conn)))

;;5. writing data back to disk
(spit "output.txt" "test")

;;6. bind out to a FileWriter and print the content,
(binding [*out* (java.io.FileWriter. "some.dat")]
  (prn {:a :b :c :d}))
