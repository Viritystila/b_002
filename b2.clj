(ns b5 (:use [overtone.live]) (:require [shadertone.tone :as t]))

(t/start "./b5.glsl" :cams [0])
(t/start "./b5.glsl" :width 1920 :height 1080 :videos ["./jkl.mp4" "./metro.mp4"] :cams [0 1]  :user-data {"iA" (atom {:synth kickf :tap "kicksaw"})})

      ;(t/s.post-start-cam 0)

(def metro (metronome 120))

(defsynth kick [amp 5.7 freq 80 fw 10 phase1 1]
  (let [src (sin-osc freq phase1)
        sawsrc (saw fw)
        _ (tap "kicksaw" 60 (a2k sawsrc))
        ]
    (out 0 (pan2 (* amp src sawsrc)))))

(def kickf (kick :amp 5 :fw 6))

(def kickf2 (kick :amp 1 :fw 1))

(def kickf3 (kick :amp 1 :fw 7))


(do (def kickfd1 (kick :amp 5 :fw 6))
    (Thread/sleep 80)
    (def kickfd2 (kick :amp 4 :fw 6 :phase 4 :freq 80)))

(kill kickfd1)

(kill kickfd2)

(kill kickf3)



(kill kickf2)

(ctl kickf :amp 3.0)


(ctl kickf :phase1 3.14)

(ctl kickf :freq 90)

(ctl kickf :fw 7)



(kill kickf)

(stop)

(defsynth doubleImpulse [amp 0.2 freq1 3 freq2 6 ]
  (let [trig1 (impulse freq1)
        trig2 (impulse freq2)]
    (out 0 (pan2 (* amp (+ trig1 trig2))))))

;(def dimp (doubleImpulse))

(kill dimp)

(defsynth bnlp [freq1 30 offset 1 amp 1 phase1 1 phase2 0 delay1 0.4 delay2 0.4 lpff 40]
  (let [src1 (sin-osc freq1 phase1)
        src2 (sin-osc (+ freq1 (* offset (sin offset))) phase2)
        snd (+ src1 src2)
        snd (delay-n snd delay1 delay2)
        snd (lpf snd lpff)]
    (out 0 (pan2 (* amp snd)))))

(def bnlp_f (bnlp))

(ctl bnlp_f :lpff 40 :freq1 50 :offset 8 :delay1 0 :phase 3.14 :amp 2.6)

(kill bnlp_f)

(defsynth nuk [amp 1 freq1 80 fw1 2 phase1 0 freq2 60 fw 2 phase2 10 rate 1]
  (let [src1 (sin-osc freq1 phase1)
        src2 (sin-osc freq2 phase2)
        src3 (phasor 0 rate 1 0 0)]
    (out 0 (pan2 (* amp (+ src1 src2 src3))))))

(def nukf (nuk))

(ctl nukf  :phase2 0 :freq1 8 :freq2 60 :amp 300 :rate 0.12)

(kill nukf)

(defsynth overpad_c [note 60 amp 0.7 attack 0.01 release 2]
  (let [freq (midicps note)
        f-env  (+ freq (* 10 freq (env-gen (perc 0.012 (- release 0.01)))))
        bfreq (/ freq 2)
        sig (apply - (concat (* 0.7 (sin-osc [bfreq (* 0.99 bfreq)])) (lpf (saw [freq (* freq 1.01)]) f-env)))]
    (out 0 (pan2 (* amp sig)))))

(def opc (overpad_c 30 :attack 10 :release 20 :amp 20))

(ctl opc :amp 3 :note 10)

(kill opc)

(def opc2 (overpad_c 50 :attack 1 :release 1 :amp 1 ))

(ctl opc2 :amp 0 :note 70 :attack 0.1 :release 0.1)

(kill opc2)

(defsynth impulser [note1 30 amp 1 duty1 1 mul1 1  note2 20 duty2 2 mul2 2 ]
  (let [freq1 (midicps note1)
        freq2 (midicps note2)
        p1 (pulse freq1)
        p2 (pulse freq2)]
    (out 0 (pan2 (distort (* amp (+ p1 p2)))))))

(def imp (impulser))


(ctl imp :amp 100.0 :note1 5 :note2 16)

(kill imp)


(defsynth sinner [freq1 40 amp 1 phase1 1 phase2 2]
  (let [sin1 (sin-osc (sin-osc (sin-osc freq1) (sin-osc phase1)) (sin-osc phase2))]
    (out 0 (pan2 (* amp sin1)))))

(def sinner1 (sinner))

(ctl sinner1 :amp 233.0 :freq1 10 :phase1 100 :phase2 2)

(kill sinner1)

(defsynth mel [amp 1 freq 1] (let [trig (impulse:kr freq)
                                       freqs (dseq [20 30 20 30 40 30 10 40] INF)
                                       note-gen (demand:kr trig 0 freqs)
                                       src (sin-osc note-gen)]
                   (out 0 (pan2 (* amp src)))))

(def melf (mel))

(ctl melf :amp 1 :freq 20)

(kill melf)

(defsynth wobbler [amp 1 freq 20 rate 5]
  (let [trig (impulse freq)
        wobble (lag (cos (phasor:ar trig rate Math/PI (* 2 Math/PI))) 0.1)
        sub (lin-lin wobble -1 1 0 1)
        sub (* sub (sin-osc freq 2))
        sub [sub sub]
        snd (+ (var-saw  freq  :width (lin-lin wobble -1 1 0.45 0.45)))
        snd (decimator snd 20000 (lin-lin wobble -1 1 0.2 0.2))
        snd (moog-ladder snd  (lin-lin wobble  -1 1 freq 2000) (lin-lin wobble -1 1 0.2 0.01))]
    (out 0 (pan2 (* amp snd)))))

(def wob (wobbler))

(ctl wob :amp 3 :rate 21 :freq 70)

(kill wob)


(defsynth overpad [note 60 amp 0.7 attack 0.001 release 2]
  (let [freq (midicps note)
        env (env-gen (perc attack release) :action FREE)
        f-env (+ freq (* 10 freq (env-gen (perc 0.012 (- release 0.01)))))
        bfreq (/ freq 2)
        sig (apply - (concat (* 0.7 (sin-osc [bfreq (* 0.99 bfreq)])) (lpf (saw [freq (* freq 1.01)]) f-env )))]
    (out 0 (pan2 (* amp env sig)))))

(overpad 30 :attack 10 :release 20)

(defsynth beep [freq 70 amp 0.1 offset 3 rate 4 depth 0.2 delay 0.3]
  (let [src (* (env-gen (perc 0.2 0.4)) (sin-osc [freq (+ offset freq)]) )
        lfo (* depth (abs (sin-osc rate)))
        del (delay-n src 2 (* lfo delay))
        env (env-gen (perc 1.2 1.6) :action FREE)]
    (out 0 (pan2 (distort (* src env amp (+ src del)))))))

(beep :amp 4)

(stop)

(defsynth beatnote [freq 50 offset 5 phase 0]
  (let [s1 (sin-osc freq 0)
        s2 (sin-osc (+ freq (sin offset)) phase)
        s3 (+ s1 s2)
        saw1 (saw 1)
        pulse1 (impulse 440 0.0)
        env (env-gen (perc 0.1 0.8 1.1 -4) :action FREE)]
    (out 0 (pan2 (* 0.6 s3 (+ pulse1 saw1) env)))))

(beatnote)

(def beatnote_d (beatnote))

(def pats {beatnote [1 1 0 1 0 1 1 0 0 0 0 1 0]
           overpad  [1 0 0 0 0 0 0 0 0 0 0 0 0]
           kick     [1 1 1 1 1 1 1 1 1 1 1 1 1]
           beep     [1 0 0 0 1 0 0 0 0 1 1 0 0]})

(def live-pats (atom pats))

(defn flatten1 [m] (reduce (fn [r [arg val]] (cons arg (cons val r))) [] m))

(defn live-sequencer
  ([curr-t sep-t live-patterns] (live-sequencer curr-t sep-t live-patterns 0))
  ([curr-t sep-t live-patterns beat]
   (doseq [[sound pattern] @ live-patterns
           :let [v (nth pattern (mod beat (count pattern)))
                 v (cond
                     (= 1 v)
                     []
                     (map? v)
                     (flatten1 v)
                     :else
                     nil)]
           :when v]
     (at curr-t (apply sound v)))
   (let [new-t (+ curr-t sep-t)]
     (apply-by new-t #'live-sequencer [new-t sep-t live-patterns (inc beat)]))))

(live-sequencer (+ 200 (now)) 200 live-pats)

(def aBeat {:freq 10})
(def bBeat {:freq 75})
(def opAmp {:amp 0.3})
(def opAmp2 {:amp 0.8 :note 40})
(def beepAmp {:amp 5})

(swap! live-pats assoc beatnote [aBeat bBeat 0 1 aBeat bBeat 1 0])
q
(swap! live-pats assoc beatnote [0])

(swap! live-pats assoc overpad [opAmp 0  opAmp2 0 opAmp 0 0 opAmp])

(swap! live-pats assoc overpad [opAmp 0 opAmp 0 ])


(swap! live-pats assoc beep [0 beepAmp 0 beeoAmp 0])

(stop)

(defn normalise-beat-info [beat]
  (cond (= 1 beat) {}
        (map? beat) beat
        (sequential? beat) beat
        :else {}))

(defn schedule-pattern [curr-t pat-dur sound pattern]
  {:pre [(sequential? pattern)]}
  (let [beat-sep-t (/ pat-dur (count pattern))]
    (doseq [[beat-info idx] (partition 2 (interleave pattern (range)))]
      (let [beat-t (+ curr-t (* idx beat-sep-t))
            beat-info (normalise-beat-info beat-info)]
        (if (sequential? beat-info)
          (schedule-pattern beat-t beat-sep-t sound beat-info)
          (at beat-t (apply sound (flatten1 beat-info))))))))

(defn live-sequencer-fixed-dur [curr-t pat-dur live-patterns]
  (doseq [[sound pattern] @live-patterns]
    (schedule-pattern curr-t pat-dur sound pattern))
  (let [new-t (+ curr-t pat-dur)]
    (apply-by new-t #'live-sequencer-fixed-dur [new-t pat-dur live-patterns])))

(live-sequencer-fixed-dur (now) 8000 live-pats)


(stop)
