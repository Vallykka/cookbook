(ns cookbook.router
  (:require [bidi.bidi :as bidi]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response]]
            [ring.middleware.params :refer [params-request]]
            [ring.util.response :refer [response not-found]]
            [cookbook.logic :as logic]
            [clojure.tools.logging :as log]
            [clojure.walk :refer [keywordize-keys]]))

(def routes
  ["/" {"list" {:get :dishes}
        ["dish/" :id]
               {#{"" "/"} {:get :dish}
                "/edit" {:post :save-dish}}
                "/delete" {:post :delete-dish}
        "add-dish" {:post :add-dish}
        ["catalog/" :type] {#{"" "/"} {:get :get-catalog}}
        true :not-found
        }])

(defmulti multi-handler :handler)

(defmethod multi-handler :dishes
  [request]
  (let [dish-name (get-in request [:query-params :name])
        resp (logic/get-dish-list dish-name)]
    (response resp)))

(defmethod multi-handler :get-catalog
  [request]
  (let [type (get-in request [:route-params :type])
        resp (logic/get-catalog type)
        ]
    (response resp)))

(defmethod multi-handler :dish
  [request]
  (let [dish-id (get-in request [:route-params :id])]
    (response (logic/get-dish-by-id dish-id))))

(defmethod multi-handler :add-dish
  [request]
  (let [params (:params request)
        {:keys [name algo ingredients]} params
        resp (logic/add-dish name algo ingredients)]
  (response resp)))

(defmethod multi-handler :save-dish
  [request]
  (let [params (:params request)
        {:keys [id name algo ingredients]} params
        resp (logic/update-dish id name algo ingredients)]
    (response resp)))

(defmethod multi-handler :delete-dish
  [request]
  (let [dish-id (get-in request [:route-params :id])
        resp (logic/delete-dish dish-id)]
    (response (str "Successfully deleted! Id - " resp))))

(defmethod multi-handler :not-found
  [request]
  (not-found "Not found"))

(defn app-handler
  [handler]
  (fn [request]
    (let [{:keys [uri]} request
          request* (bidi/match-route* routes uri request)]
      (handler request*))))

(defn keywordize-query-params
  [handler]
  (fn [request]
    (handler (keywordize-keys (params-request request)))))

(defn exception-wrapper
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (log/errorf "Exception occured: %s" (ex-message e))
        {:status 500
         :headers {"content-type" "application/json"}
         :body (ex-message e)
         }))))

(def app (-> multi-handler
             app-handler
             keywordize-query-params
             wrap-json-params
             exception-wrapper
             wrap-json-response))