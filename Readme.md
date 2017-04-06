#1. ThreadRaindrop

##1.1 출력 화면

![](http://i.imgur.com/272asy2.png)

버튼은 3가지입니다. (START, PAUSE, STOP)

- START : 총 3가지의 thread가 동작합니다.(화면을 다시 그려주는 thead , 물방울 하나를 계속 만들어내는 thread, 물방울 하나당 y축 좌표만을 바꾸는 thread)

- PAUSE : 화면을 정지시킵니다. ( 이 때, 모든 thread의 while loop가 정지됩니다.)

- STOP : 화면을 리셋시키면서 Thread를 메모리에서 제거합니다.

##1.2 코드

이 프로젝트에서는 MainActivity.java 하나의 파일로 구성하였습니다.

###1.2.1 onCreate()

![](http://i.imgur.com/s8pOFG6.png)

(1) 모바일 폰의 화면 사이즈 값을 가로(deviceWidth)와 세로(deviceHeight)에 각각 받습니다.

(2) 위젯과 아이디를 서로 연결하고 버튼 리스너를 생성합니다.

(3) stage는 비를 표현(물방울)하는 객체를 화면에 그려줍니다. 

(4) layout은 버튼 상단에 위치하여 비가 그려지는 frameout입니다. 여기에 stage를 담아주기(그려주기)위해서는 아래 명령을 해주어야 합니다.

			     		layout.addView(stage) 

###1.2.2 stage 클래스

![](http://i.imgur.com/jH4vNFD.png)
![](http://i.imgur.com/0jkQXN2.png)

(1) Stage constructor :  물방울 객체(raindrops)를 담을 ArrayList를 선언하고
각 방울의 색깔(rainColor)는 파랑색으로 설정합니다.

>> 왜 CopyOnWriteArrayList()를 사용하는가?
>
>> onDraw()에서 ArrayList에 담은 물방울을 모두 그려주기 위해서 동적 loop를 사용하였습니다. 하지만 이때 ArrayList의 사이즈가 달라질 위험이 있기 때문에 해당 데이터를 안전하게 읽고 처리하기 위해 CopyOnWriteArrayList()를 사용합니다.

>> CopyOnWriteArrayList()는 안의 내용을 읽을 때, 데이터를 복사해 놓고 읽습니다.


(2) onDraw() : raindrops에 담긴 물방울을 화면 좌표상에 그립니다.

(3) addRaindrop() : raindrops에 새로 생성한 물방울을 추가하는 메소드.

(4) removeRaindrop() : 물방울이 화면의 사이즈를 벗어났거나 유저가 상황을 종료시킬 때 호출되는 메소드. 물방울을 raindrops에서 제거하고 해당하는 물방울 쓰레드를 종료시킵니다.

(5) removeAll() : (4)번이 하나의 물방울만을 제거하지만, 이 메소드가 호출되면 모든 물방울이 제거되면서 모든 물방울 쓰레드를 종료시킵니다.

###1.2.3 RedawThread // 쓰레드

![](http://i.imgur.com/APUObHu.png)

--> thread 생성시, Stage 인스턴스를 받습니다. 이 인스턴스는 0.05초 간격으로 화면(view)을 계속 새롭게 그리는데 사용합니다.

				stage.postInvalidate();



###1.2.4 MakeRain // 쓰레드

![](http://i.imgur.com/5yyQu0k.png)

--> thread 생성시, Stage 인스턴스를 받습니다. 0.05초 간격으로 새로운 물방울을 만듭니다.
(Raindrop 쓰레드를 호출합니다.)


###1.2.5 Raindrop // 쓰레드


![](http://i.imgur.com/7pcyCUd.png)
![](http://i.imgur.com/eGaKh0n.png)

--> 물방울 하나에 대한 정보를 가집니다. 

(1) 물방울이 떨어질 x, y좌표와 반지름, 떨어지는 속도를 생성자에서 결정합니다. 자기 자신을 stage의 ArrayList에 저장합니다.

(2) y 좌표에 떨어지는 속도를 매번 갱신합니다.

(3) y 좌표가 화면 사이즈를 벗어나면 stage의 ArrayList에서 제거합니다.

###1.2.6 onClick()

![](http://i.imgur.com/9u8RjOd.png)
![](http://i.imgur.com/UMR1I63.png)

(1) START 버튼 : RedrawThread와 MakeRain 쓰레드 를 동작시킵니다.

(2) PAUSE 버튼 : flag 값을 바꾸어 각 쓰레드가 동작하지 못하도록 막습니다.

(3) STOP 버튼 : flag 값을 바꾸고 각 쓰레드를 메모리에서 제거한뒤 화면을 초기화 시킵니다.

###1.2.7 onDestroy()

![](http://i.imgur.com/c04Pxis.png)

--> 사용자가 실행되고 있는 애플리케이션에서 나갈 경우, onDestroy()에서는 1.2.6 onClick()의 STOP 버튼과 같은 역할을 합니다. 