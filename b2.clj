(use 'overtone.live)


(definst ss [freq1 100] (* (sin-osc freq1) (saw freq1)) )

(use 'shadertone.tone)



(defsynth vvv
  [freq1 0.3 freq2 0.2]
  (let [a (+ 300 (* 50 (sin-osc:kr freq1)))
        b (+ 300 (* 100 (sin-osc:kr freq2)))
        _ (tap "a" 60 (a2k a))
        _ (tap "b" 60 (a2k b))]
    (out 0 (pan2 (+ (sin-osc a)
(sin-osc b))))))

(def v (vvv))




(shadertone.tone/start "sine_dance.glsl" :textures ["L1030941.JPG"]
                       :user-data {"iA" (atom {:synth v :tap "a"})
                                   "iB" (atom {:synth v :tap "b"})})

(vvv)
(ctl v :freq 100)
