(defproject cookbook "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [mount "0.1.16"]
                 [cprop "0.1.17"]
                 [org.apache.logging.log4j/log4j-api "2.13.3"]
                 [org.apache.logging.log4j/log4j-core "2.13.3"]
                 [org.apache.logging.log4j/log4j-slf4j-impl "2.13.3"]
                 [com.fasterxml.jackson.core/jackson-databind "2.11.3"]
                 [bidi "2.1.6"]
                 [ring/ring-core "1.8.1"]
                 [ring/ring-jetty-adapter "1.8.1"]
                 [ring/ring-json "0.5.0"]
                 [clj-postgresql "0.7.0" :exclusions [org.clojure/java.data org.clojure/java.jdbc]]
                 [conman "0.9.0"]
                 [migratus "1.2.8"]
                 [org.clojure/core.specs.alpha "0.2.44"]
                 [org.clojure/test.check "1.1.0"]]
  :plugins [[migratus-lein "0.7.3"]
            [io.taylorwood/lein-native-image "0.3.0"]
            [nrepl/lein-nrepl "0.3.2"]]
  :main ^:skip-aot cookbook.core
  :aot [cookbook.core]
  :repl-options {:init-ns cookbook.core}
  :profiles {:uberjar {:source-paths ["src"]
                       :resource-paths ["resources"]
                       :aot :all}
             :test          [:project/test :profiles/test]
             :project/test {:resource-paths ["test/resources"]}
             :profiles/test {}
             }
  :native-image {:name     "cookbook"
                 :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                 :opts     ["--enable-url-protocols=http"
                            "--report-unsupported-elements-at-runtime"
                            "--allow-incomplete-classpath"
                            "--no-server"
                            "-J-Xmx3g"
                            ]}
)