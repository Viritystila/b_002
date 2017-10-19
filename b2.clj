(use 'overtone.live)





(definst ss [freq1 100] (* (sin-osc freq1) (saw freq1)) )

(use 'shadertone.tone :reload-all)

;;(use 'shadertone.shader :reload-all)

(defsynth vvv
  [freq1 0.3 freq2 0.29]
  (let [a (+ 30 (* 20 (sin-osc:kr freq1)))
        b (+ 30 (* 10 (sin-osc:kr freq2)))
        _ (tap "a" 60 (a2k a))
        _ (tap "b" 60 (a2k b))]
    (out 0 (pan2 (+ (sin-osc a)
(sin-osc b))))))

(def v (vvv))




(shadertone.tone/start "sine_dance.glsl" :width 3000 :height 2000 :cams [3 4]
                       :user-data {"iA" (atom {:synth v :tap "a"})
                                 "iB" (atom {:synth v :tap "b"})})



(shadertone.shader/stop)


(vvv)


(definst beat [f1 1] (* 10.0(sin-osc f1) (saw 0.5)))

(beat)

(ctl beat :f1 20)


(require '[leipzig.melody :refer [all bpm is phrase tempo then times where with]])


(def melody (phrase [1 2 2 3 2 1]
                    [2 1  1 1 1 1]))


(require '[leipzig.live :as live]
         '[leipzig.scale :as scale])


(definst beeb [freq 440 dur 0.1]
  (-> freq
   sin-osc
   (* (env-gen (perc 0.05 dur) :action FREE))))

(beeb)


(defmethod live/play-note :default  [{midi :pitch seconds :duration}]
  (-> midi overtone.live/midi->hz ( / 3) (beeb seconds)))


(->> melody (tempo (bpm 90))
     (where :pitch (comp scale/C scale/major))
     live/play)
