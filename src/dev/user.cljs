(ns user
  (:require [thi.ng.geom.core :as geom]
            [thi.ng.math.core :as m]
            [thi.ng.geom.types :as geom-types]
            [thi.ng.geom.path :as paths]
            [thi.ng.geom.bezier :as bezier]
            [thi.ng.geom.circle :as circle]
            [thi.ng.geom.svg.core :as svg]
            [thi.ng.geom.svg.adapter :as svg-adapter]
            [thi.ng.geom.svg.renderer :as svg-renderer]
            [thi.ng.geom.viz.core :as viz]
            [thi.ng.color.core :as color]
            [thi.ng.color.gradients :as grad]))

#_(def spec
   {:x-axis (viz/log-axis
             {:domain [1 201]
              :range  [50 590]
              :pos    550})
    :y-axis (viz/linear-axis
             {:domain      [0.1 100]
              :range       [550 20]
              :major       10
              :minor       5
              :pos         50
              :label-dist  15
              :label-style {:text-anchor "end"}})
    :grid   {:attribs {:stroke "#caa"}
             :minor-x true
             :minor-y true}
    :data   [{:values  (map (juxt identity #(Math/sqrt %)) (range 0 200 2))
              :attribs {:fill "#0af" :stroke "none"}
              :layout  viz/svg-scatter-plot}
             {:values  (map (juxt identity #(m/random %)) (range 0 200 2))
              :attribs {:fill "none" :stroke "#f60"}
              :shape   (viz/svg-triangle-down 6)
              :layout  viz/svg-scatter-plot}]})

#_(def mypath  "M 604,1.1 C 601.5,1.6 586.6,3.4 570.7,5 530.3,9.3 506,13.8 468,24.2 311.9,66.6 170.6,174.9 89.1,314.5 40.2,398.1 11,492.4 3.6,589.8 c -4.5,59.8 -4.6,55.3 0.7,105.7 5.1,47.7 5.3,49.8 8.8,68 15,78.8 49,162 94.2,230 39.5,59.5 91.4,114 152.1,159.9 64,48.4 147.5,87.3 230,107.1 25.8,6.2 53,10.6 95.1,15.2 27.5,3.1 71.4,4.3 81.8,2.3 3.2,-0.6 14.2,-1.5 24.5,-2 30.5,-1.4 73.5,-8.5 112.7,-18.5 84.4,-21.5 168.1,-63.8 238.5,-120.4 109.3,-87.9 185.6,-207.5 218.5,-342.6 8,-32.5 12.5,-61 17.2,-108 2.4,-23.1 2.4,-71.8 0,-93.5 -0.9,-8.5 -2.4,-22.3 -3.2,-30.5 -7.9,-72.8 -30.9,-147.2 -67.4,-218 C 1123.5,182.4 975.8,65.3 798.5,20.7 759.5,10.9 732.7,6.7 680.5,2.3 658.6,0.4 611.2,-0.3 604,1.1 Z m 72,61.4 c 101,6.3 199,38.9 283.5,94.2 74.4,48.8 138.8,117.5 182.8,194.9 31.4,55.3 55.1,118.8 66.2,177.5 4.2,22.4 10.5,88.1 10.5,110.1 0,12.1 -0.4,18.9 -1.1,19.3 -1.8,1.2 -4.6,-2.4 -9.4,-11.6 -7.6,-14.7 -13.5,-32.4 -25.5,-76 -12.4,-45.1 -20.5,-65.6 -33.8,-85.5 -9.1,-13.5 -17.9,-21.4 -30.4,-27.3 -12.8,-6.1 -27.1,-11.4 -39.1,-14.6 -12.1,-3.2 -51.1,-10 -66.2,-11.5 -6.6,-0.7 -13.1,-1.8 -14.5,-2.5 -1.4,-0.7 -5.9,-4 -9.9,-7.4 -9.7,-7.8 -12.5,-9.1 -20.4,-9.1 -10.2,0 -16,-1.6 -24,-6.4 -15,-9.1 -15.8,-9.2 -24.6,-2.6 -2.9,2.2 -6.1,4 -7.1,4 -1,0 -4.1,-2.7 -7,-6 -6.9,-8 -11,-10.4 -16.2,-9.7 -4.7,0.6 -7.5,3 -12.4,10.8 -2,3.1 -4.8,6.3 -6.2,7.1 -2.7,1.4 -2.4,1.4 -32.7,0.6 -22.9,-0.5 -26.4,-1.3 -38,-8.1 -10.5,-6.2 -19.9,-7.4 -29.9,-3.7 -21.9,8.3 -26.3,41.5 -7.9,61.1 8,8.6 22.4,14.4 33.7,13.7 5.3,-0.3 6.9,-1 12.2,-4.7 5.3,-3.7 6.6,-4.2 9.4,-3.6 4.8,1.1 14.3,11.1 21.9,23.2 6.9,11 15.4,20.4 21.1,23.3 1.9,0.9 12.4,3.4 23.4,5.5 10.9,2 22,4.3 24.7,5.1 6.4,1.9 41.2,18.9 51.9,25.4 4.7,2.8 10,6.5 11.8,8.3 6.3,6.1 14.7,25.9 22.6,53.2 7.2,24.7 11.5,48.3 15.7,86 1.1,9.9 3.3,27.2 4.9,38.3 1.7,11.2 3,24.4 3,29.3 0,4.9 0.7,13.5 1.6,19.2 0.8,5.6 1.6,17.8 1.7,27.2 0.2,21 -0.4,23 -7.3,22.9 -5,-0.1 -26.7,-7.9 -41.9,-15 -10.3,-4.8 -24.4,-13.4 -33.1,-20 -5.5,-4.2 -20.9,-19.4 -25,-24.7 -1.2,-1.5 -8.5,-8.4 -16.2,-15.3 -7.7,-6.9 -17.7,-16.7 -22.1,-21.7 -4.7,-5.4 -13.1,-13.2 -20.4,-18.9 -6.8,-5.4 -18,-14.6 -24.8,-20.5 -11.2,-9.6 -13.4,-11.1 -22.7,-14.9 -5.7,-2.3 -15.5,-6.7 -21.8,-9.8 -17.5,-8.5 -16.8,-8.4 -68,-7.2 -51.4,1.3 -56.9,-0.1 -81.5,-19.8 -3.8,-3.1 -10.1,-7.5 -13.8,-9.7 -3.7,-2.2 -8.3,-5.4 -10.1,-6.9 -7.3,-6.5 -11.9,-24.1 -9.8,-37.9 1.9,-12.9 7.8,-22.4 17,-27.4 7.1,-3.8 26.7,-7.8 43.7,-8.7 16.4,-0.9 21.9,-2.6 28.1,-8.5 4.9,-4.7 7.4,-11.7 7.4,-20.6 0,-4.8 -0.5,-6.4 -3.3,-10.5 -5.9,-8.6 -6.8,-11.2 -8.3,-24.1 -1.7,-14.8 -3.2,-18.2 -10.7,-24.6 -9.1,-7.7 -10,-12.1 -8.3,-38.2 l 0.8,-12 -3.6,-9.2 c -2,-5.1 -3.6,-10.3 -3.6,-11.6 0,-1.3 1.8,-6 4,-10.6 5.6,-11.5 5.4,-14.6 -0.7,-21.3 -2.8,-2.9 -12.1,-9.9 -21.9,-16.3 -9.8,-6.5 -18.3,-12.8 -19.9,-14.9 -1.5,-1.9 -4.4,-8.5 -6.6,-14.6 -6.6,-18.7 -8,-20.6 -28.6,-39.5 -10.8,-9.8 -16.7,-22.3 -23.4,-49 -8,-32.2 -11.5,-39.9 -25.5,-57.4 -10.7,-13.3 -13,-14.8 -21.5,-13.8 -2.6,0.3 -7.5,2 -10.8,3.6 l -6,3.1 -8.8,-2.1 c -9.4,-2.3 -25.9,-3.6 -31.6,-2.5 -5.1,1 -8.4,4.9 -10.7,12.7 -1.2,3.8 -3,7.8 -4.2,8.8 -1.1,1.1 -7.3,4 -13.7,6.5 -15.1,5.9 -18.8,8.2 -41.6,26 -10.4,8.1 -23.7,17.6 -29.5,20.9 -7.8,4.5 -12.7,8.4 -19.1,14.7 -12.4,12.5 -27.5,30.5 -31.4,37.5 -7.1,12.7 -12,28.5 -14.6,47 -2.5,18.4 -1.3,72.8 2.2,91.6 2.8,15.3 3.9,18.4 12.6,34.6 7.4,13.8 8.5,15.4 21.7,29.2 17.3,18.3 25.1,27.9 28.8,35.6 3.3,7.1 3.5,10.6 0.8,21.1 -3.3,13.1 -6.6,18.4 -23.1,36.9 -19.4,21.8 -27.3,31.9 -30.5,39.2 -4.3,9.5 -8.1,20.5 -8.9,25.3 -1.4,9.1 -2.6,10.3 -18.9,20.5 -18.1,11.3 -28.1,19.1 -40.1,31.6 -12,12.4 -17.3,20.8 -25.5,39.7 -3.7,8.6 -9.1,20.6 -11.9,26.7 -2.9,6 -7.4,17.3 -10.1,25 -2.6,7.7 -9.1,25.3 -14.3,39.1 -5.2,13.8 -14,39.4 -19.5,57 -13.9,43.8 -14.6,45.9 -16.5,45.9 -2.9,0 -8,-8.1 -19.6,-31.5 C 107.4,867.4 97.7,843.3 87,808.5 74,766.3 68.5,735.6 63.6,678.5 61.4,653.2 61.3,648.1 62.1,626 63.8,577.9 68.4,544.3 79.5,499.5 105.9,392.8 157.9,301.8 236.6,225 298.9,164.2 368.6,121.3 450.4,93.4 525.3,67.9 599.6,57.7 676,62.5 Z m 442.5,407 c 3.8,1.7 8.1,5.8 15.2,14.8 3.1,4 8.9,10.6 12.8,14.7 12.3,13 18.8,29.2 33.6,83.5 4.4,16.5 9.4,33.9 11,38.7 3.8,11.3 12.3,29.4 19.6,41.6 7,11.8 7.2,13.4 4.4,40.8 -3.3,32.4 -8,57.4 -16.6,88.9 -8.8,32.2 -25.3,76.6 -29.8,80.4 -2.1,1.6 -0.8,5.1 -11.3,-30.9 -4.7,-16.2 -13.9,-45.9 -20.4,-66 -6.5,-20.1 -12.8,-40.3 -14,-45 -1.2,-4.7 -5.2,-18.9 -9,-31.5 -3.8,-12.7 -11.6,-39.3 -17.4,-59.2 -11.2,-38.4 -19,-63.4 -22.5,-72 -1.1,-2.8 -2.5,-8.2 -3.1,-12 -2.2,-13.1 -8,-32.9 -13.6,-46.3 -10.3,-24.8 -14.3,-38.2 -12.3,-40.6 1.8,-2.1 4.2,-2.2 37.9,-1.7 23.4,0.4 33.3,0.9 35.5,1.8 z m -203,481.9 c 1.9,0.8 5.4,2.8 7.7,4.4 16.7,11.4 113.4,47.3 147.7,54.9 4.6,1 8.6,2.4 8.8,3.1 1.9,4.9 -45.3,52.1 -78.7,78.7 -34.5,27.5 -74.8,52.4 -114.5,70.8 -41.8,19.4 -82.3,33.1 -84.9,28.8 -1.2,-1.9 -5.6,-55.4 -5.6,-68 0,-7.5 0.7,-25.5 1.6,-40.1 0.8,-14.6 1.9,-44 2.3,-65.3 0.7,-30.8 1.1,-39.2 2.2,-40.2 3.2,-2.9 22,-10.7 27.2,-11.2 7,-0.7 10.4,0.4 25.7,8.8 14.6,7.9 20.3,9.7 25.9,8 5.3,-1.5 11.5,-8.6 18.1,-20.5 3.5,-6.3 6.6,-10.5 8.5,-11.7 3.6,-2.3 3.7,-2.3 8,-0.5 z")

#_(def mybezier (bezier/auto-spline2 (paths/parse-svg-coords mypath)))


#_(svg/svg {} [:p {:d mybezier}])
