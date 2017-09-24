(use 'overtone.live)


(definst ss [freq1 100] (* (sin-osc freq1) (saw freq1)) )

(use 'shadertone.tone)



(defsynth vvv
  [freq1 0.3 freq2 0.29]
  (let [a (+ 30 (* 20 (sin-osc:kr freq1)))
        b (+ 30 (* 10 (sin-osc:kr freq2)))
        _ (tap "a" 60 (a2k a))
        _ (tap "b" 60 (a2k b))]
    (out 0 (pan2 (+ (sin-osc a)
(sin-osc b))))))

(def v (vvv))




(shadertone.tone/start "sine_dance.glsl" :width 2000 :textures ["L1030941.JPG"]
                       :user-data {"iA" (atom {:synth v :tap "a"})
                                   "iB" (atom {:synth v :tap "b"})})

(vvv)


(definst beat [f1 1] (* 10.0(sin-osc f1) (saw 0.5)))
(beat)
