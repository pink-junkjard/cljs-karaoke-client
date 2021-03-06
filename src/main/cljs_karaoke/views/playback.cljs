(ns cljs-karaoke.views.playback
  (:require [re-frame.core :as rf]
            [cljs-karaoke.subs :as s]
            [cljs-karaoke.components.progress-bar :refer [progress-bar-component]]
            [cljs-karaoke.router.core :as router]
            [cljs-karaoke.subs.audio :as audio-subs]
            [cljs-karaoke.events.audio :as audio-events]
            [cljs-karaoke.events.songs :as song-events]
            [cljs-karaoke.events.playlists :as playlist-events]
            [stylefy.core :as stylefy]
            [cljs-karaoke.events.backgrounds :as bg-events]
            [cljs-karaoke.playback :as playback :refer [play pause stop]]
            [cljs-karaoke.styles :as styles
             :refer [time-display-style centered
                     top-left parent-style shadow-style
                     top-right logo-bg-style]]
            [cljs-karaoke.modals :refer [show-export-text-info-modal]]
            [cljs-karaoke.utils :as utils :refer [icon-button]]
            [goog.string :as gstr :refer [urlEncode format]]))

(defn lyrics-timing-progress []
  (let [time-remaining (rf/subscribe [::s/time-until-next-event])]
    (fn []
      [progress-bar-component
       :max-value 3000
       :style {:margin   "0 0.5rem"
               :width    "calc(100% - 1rem)"
               :position :absolute
               :bottom   "0.8rem"
               :left     0}
       :current-value (- 3000
                         (if (> @time-remaining 3000)
                           3000
                           @time-remaining))
       :value-bar-style {:background-color :hotpink}])))

(defn song-progress []
  (let [dur (rf/subscribe [::s/song-duration])
        cur (rf/subscribe [::s/song-position])]
    (fn []
      [progress-bar-component
       :max-value (if (number? @dur) @dur 0)
       :current-value (if  (number? @cur) @cur 0)
       :color :blue
       :label (format "%d%%" (int (* 100 (/ @cur @dur))))
       :style {:position :absolute
               :display :block
               :left 0
               :bottom "0.1rem"
               :height "0.4rem"
               :margin "0 0.5rem"
               :width "calc(100% - 1rem)"}])))

(defn seek [offset]
  (let [audio            (rf/subscribe [::s/audio])
        pos              (rf/subscribe [::s/player-current-time])]
    (set! (.-currentTime @audio) (+ @pos (/ (double offset) 1000.0)))))

(defn increase-playback-rate-btn []
  (let [r (rf/subscribe [::s/audio-playback-rate])]
    [icon-button "plus" "default" #(rf/dispatch [::song-events/set-audio-playback-rate (if (>= @r 2) 2 (+ @r 0.1))])]))

(defn decrease-playback-rate-btn []
  (let [r (rf/subscribe [::s/audio-playback-rate])]
    [icon-button "minus" "default" #(rf/dispatch [::song-events/set-audio-playback-rate (if (<= @r 0.1) 0.1 (- @r 0.1))])]))

(defn song-time-display [^double ms]
  (let [secs  (-> ms
                  (/ 1000.0)
                  (mod 60.0)
                  long)
        mins  (-> ms
                  (/ 1000.0)
                  (/ (* 60.0 1.0))
                  (mod 60.0)
                  long)
        hours (-> ms
                  (/ 1000.0)
                  (/ (* 60.0 60.0 1.0))
                  (mod 60.0)
                  long)]
    [:div.time-display
     (stylefy/use-style time-display-style
                        (merge
                         {}
                         (if @(rf/subscribe [::audio-subs/recording?])
                           {:class "has-text-danger has-background-light"} {})))
     [:span.hours (format "%02d" hours)] ":"
     [:span.minutes (format "%02d" mins)] ":"
     [:span.seconds (format "%02d" secs)]]))
(defn show-sharing-url []
  (let [song-name (rf/subscribe [::s/current-song])]))

(defn- load-random-song []
  (rf/dispatch [::song-events/trigger-load-random-song]))

(defn- clear-cached-song-bg-image [song]
  (rf/dispatch [::bg-events/forget-cached-song-bg-image song]))

(defn options-menu-entry [{:keys [label icon on-click]}]
  [:a.dropdown-item
   {:href "#"
    :on-click on-click}
   [:i.fas.fa-fw {:class [icon]}] label])
(defn more-options-menu []
  [:div.dropdown-menu {:role :menu}
   [:div.dropdown-content]])

(defn playback-controls []
  [:div.playback-controls.field.has-addons
   (stylefy/use-style shadow-style)
   (when-not (= :playback @(rf/subscribe [::s/current-view]))
     [icon-button "play" "primary" play])
   (when @(rf/subscribe [::s/display-home-button?])
     [:div.control>a.button.is-small.is-default
      {:href (router/url-for :home)}
      [:i.fas.fa-home.fa-fw]])
   (when-not @(rf/subscribe [::s/song-paused?])
     [icon-button "pause" "warning" pause])
   (when-not @(rf/subscribe [::s/song-paused?])
     [icon-button "stop" "danger" stop])
   (when (and @(rf/subscribe [::audio-subs/audio-input-available?])
              @(rf/subscribe [::audio-subs/recording-enabled?]))
     [icon-button "circle" "info" #(rf/dispatch [::audio-events/test-recording])
      (rf/subscribe [::audio-subs/recording-button-enabled?])])
   [icon-button "step-forward" "info" #(do
                                         (stop)
                                         (rf/dispatch [::playlist-events/playlist-next]))]
   [icon-button "random" "warning" load-random-song]
   [increase-playback-rate-btn]
   [decrease-playback-rate-btn]
   [icon-button "share-alt" "success"
    #(show-export-text-info-modal
      {:title "Share Link"
       :text (str "https://karaoke.uyuyuy.xyz/songs/" (urlEncode @(rf/subscribe [::s/current-song])) ".html")})]])
