; ======================================================================================== 
; POCKET MOD		par Mr Ouarga.K ( alias +OK/KO- , alias BobyC [BobySait] ) 
; ======================================================================================== 
; /!\ ressources originalement créées pour le projet MDT. 
; ------------------------------------------------------- 
; 1	Pour toute information non implicite et/ou non expliquée, veuillez contacter l'auteur 
;	par l'intermédiaire du forum www.BlitzFr.com 
;	par l'intermédiaire du forum www.Blitz3dFr.com 
;	en le contactant par email : BobySait@free.fr 
; ------------------------------------------------------- 
; 2	Toutes les fonctions / librairies ci dessous sont le produit des travaux de BobyC (BobySait) 
; 
;	Nul ne pourrait s'en attribuer le mérite, ni même modifier les sources afin de les redistribuer 
;	Il est IMPÉRATIF de citer l'auteur dans vos travaux Freeware 
;	Il est IMPÉRATIF de citer l'auteur dans un cadre Shareware et/ou N'importeQuoiDAutreWare ! 
; ======================================================================================== 
 
AppTitle "~ POCKET MOD - par BobyC ~" 
 
Graphics3D 1024,768,0,2 
SetBuffer BackBuffer() 
 
; ------------------ 
; - CONFIG TOUCHES - 
; ------------------ 
;	Global KEY_VRT%		=	47 	; V 
;	Global KEY_TRI%		=	20	; T 
 
;	Global KEY_VIEWROT%	=	56	; Alt + MouseDown(MOS_VIEWROT) 
;	Global MOS_VIEWROT%	=	2	; Right Mouse Down +> Rotation vue 
	Global MOS_SELECT%	=	1	; Left Click +> Select 
 
; --------------- 
; - CONFIG MODE - 
; --------------- 
	Const MODE_WORLD%=0; selection d'objet 
	Const MODE_OBJ%=1	; modif d'objet 
;	Const MODE_VRT%=2	; modif des vertices 
;	Const MODE_TRI%=3	; modif des tris 
 
	Global Mode%=MODE_WORLD 
 
; ---------------- 
; - CONFIG ROOTS - 
; ---------------- 
	Global WORLD_OBJ%=CreatePivot():NameEntity(WORLD_OBJ,"[+++]OBJ") 
	Global WORLD_IDE%=CreatePivot():NameEntity(WORLD_IDE,"[+++]IDE") 
 
 
; ----------------- 
; - GamePlay View - 
; ----------------- 
	p1=CreatePivot() 
	p2=CreatePivot(p1) 
	cam=CreateCamera(p2) 
		MoveEntity(cam,0,0,-10) 
		TurnEntity p1,0,45,0 
		TurnEntity p2,45,0,0 
 
; ------------------ 
; - Scene Minimale - 
; ------------------ 
	; - Sol - 
	plan=CreatePlane():EntityColor plan,20,50,0 

	; - Picker - 
	Picker=CreatePicker3Plans() 
 
; -------------------- 
; - Demo -> Cube ... - 
; -------------------- 
	cube=CreateCube():AddObject(cube) 
 
 
 
Repeat 
	msx#=MouseXSpeed() 
	msy#=MouseYSpeed() 
 
	If KeyHit(KEY_VRT) 
		If Mode=MODE_OBJ Or Mode=MODE_TRI	Mode=MODE_VRT 
	EndIf 
 
	If KeyHit(KEY_TRI) 
		If Mode=MODE_OBJ Or Mode=MODE_VRT	Mode=MODE_TRI 
	EndIf 
 
	If KeyDown(KEY_VIEWROT) 
		If MouseDown(MOS_VIEWROT) 
			TurnEntity p1,0,-msx,0 
			TurnEntity p2,+msy,0,0 
		EndIf 
	EndIf 
 
	Local MOVEOBJ%=MouseDown(MOS_SELECT) 
	Local CLICKOBJ%=MouseHit(MOS_SELECT) 
	If CLICKOBJ MOVEOBJ=0 
 
	; GamePlay 
		; si on est en "transformation" +> on se fou du picking ! 
		If SELECTED_AXE<>0 And MOVEOBJ>0 
			TFormVector +msx,-msy,0,cam,0 
			Select EntityName(SELECTED_AXE) 
				Case "[AXE]X"	:vx#=1:vy#=0:vz#=0 
				Case "[AXE]Y"	:vx#=0:vy#=1:vz#=0 
				Case "[AXE]Z"	:vx#=0:vy#=0:vz#=1 
				Case "[AXE]XZ"	:vx#=1:vy#=0:vz#=1 
				Case "[AXE]XY"	:vx#=1:vy#=1:vz#=0 
				Case "[AXE]YZ"	:vx#=0:vy#=1:vz#=1 
			End Select 
			dd#=Float(EntityDistance(cam,SELECTED_OBJ)*2)/(GraphicsWidth()) 
			TranslateEntity SELECTED_OBJ,vx*TFormedX()*dd,vy*TFormedY()*dd,vz*TFormedZ()*dd,True 
			PositionEntity Picker,EntityX(SELECTED_OBJ,1),EntityY(SELECTED_OBJ,1),EntityZ(SELECTED_OBJ,1) 
		; sinon test nouveau pick 
		Else 
			; prepare le picking en fonction du mode 
			Select Mode 
				Case MODE_WORLD 
					HideEntity Picker 
				Case MODE_OBJ 
					ShowEntity Picker 
					HideEntity WORLD_OBJ 
				Case MODE_VRT 
					ShowEntity Picker 
					HideEntity WORLD_OBJ 
				Case MODE_TRI 
					ShowEntity Picker 
					HideEntity WORLD_OBJ 
			End Select 
 
			; Picking 
			pick=CameraPick(cam,MouseX(),MouseY()) 
 
			; restore le visu 
			ShowEntity(WORLD_OBJ) 
			Select Mode 
				Case MODE_WORLD 
					HideEntity Picker 
				Case MODE_OBJ 
					ShowEntity Picker 
				Case MODE_VRT 
					ShowEntity Picker 
				Case MODE_TRI 
					ShowEntity Picker 
			End Select 
 
			; pick "réussi" 
			If pick 
				name$=EntityName(pick) 
				Select Upper(GetTag(pick,5)) 
					Case "[AXE]" 
						If AXE_PICK EntityAlpha AXE_PICK,.5:EntityBlend AXE_PICK,1 
						If CLICKOBJ	SELECTED_AXE=Pick:DebugLog "Axe Pick" 
						AXE_PICK=Pick 
						EntityAlpha AXE_PICK,.75:EntityBlend AXE_PICK,3 
					Case "[OBJ]" 
						If CLICKOBJ 
							Mode=MODE_OBJ 
							SELECTED_OBJ=Pick 
							PositionEntity Picker,EntityX(Pick,1),EntityY(Pick,1),EntityZ(Pick,1) 
						EndIf 
					Default 
						; si pick dans le vide +> deselectionne l'objet. 
						If CLICKOBJ 
							If Mode=MODE_OBJ	Mode=MODE_WORLD 
						EndIf 
				End Select 
 
			; pick "vide" 
			Else 
				If AXE_PICK<>0 
					EntityAlpha AXE_PICK,.5 
					EntityBlend AXE_PICK,1 
					AXE_PICK=0 
				EndIf 
				If CLICKOBJ 
					If SELECTED_AXE<>0 
						EntityAlpha SELECTED_AXE,.5 
						EntityBlend SELECTED_AXE,1 
						SELECTED_AXE=0 
					EndIf 
					If Mode=MODE_OBJ Mode=MODE_WORLD 
				EndIf 
			EndIf 
		EndIf 
 
	ShowEntity WORLD_OBJ 
 
	RenderWorld 
		Text 10,10,"MODE : "+Mode 
		If SELECTED_AXE<>0 Text 150,10," Axe :"+EntityName(SELECTED_AXE) 
	Flip 
 
Until KeyHit(1) 
End 
 
Function GetTag$(obj,nbChar%=5) 
	If Len(EntityName(obj))<nbChar Return "" 
	Return Left(EntityName(obj),5) 
End Function 
 
Function AddObject(obj%) 
	EntityPickMode	(obj,2) 
	NameEntity		obj,"[OBJ]"+EntityName(obj) 
 
	Parent%=GetParent(obj) 

	Repeat 
		If Parent=WORLD_OBJ Parent_=GetParent(obj):Exit 
		If Parent=0 Parent_=0:Exit 
		Parent=GetParent(parent) 
	Forever 

	If Parent_=0 Then EntityParent(obj,WORLD_OBJ) 
End Function 
 
Function CreatePicker3Plans%() 
	; Pivot 
	Axe	=	CreatePivot(WORLD_IDE) 
 
	; visu +> texture 
	tex	=	CreateTexture	(64,64,3) 
	buf	=	TextureBuffer	(tex) 

	COLLINE=200 Shl(24)+ 255 Shl(16) + 255 Shl (8)+255 
	COLFOND=100 Shl(24)+ 255 Shl(16) + 255 Shl (8)+255 

	SetBuffer		TextureBuffer(tex) 
	LockBuffer		(buf) 
		For i =1 To 62	For j =1 To 62	WritePixelFast i,j,COLFOND	Next Next 
		For i = 0 To 63 
			WritePixelFast i,0,COLLINE 
			WritePixelFast 0,i,COLLINE 
			WritePixelFast 63-i,i,COLLINE 
		Next 
		For i = 1 To 62 
			WritePixelFast 62-i,i,COLLINE 
		Next 
	UnlockBuffer	(buf) 
	SetBuffer		(BackBuffer()) 
 
	; pickable +> 3 "plans" + 3 axes 
	PlanXZ=	CreateMesh		(Axe) 
			EntityPickMode	(PlanXZ,2) ; Sol 
			EntityColor		(PlanXZ,255,000,000) 
			EntityFX		(PlanXZ,1+8) 
			EntityAlpha		(PlanXZ,.5) 
			NameEntity		(PlanXZ,"[AXE]XZ") 
			EntityTexture	(PlanXZ,tex) 
			EntityOrder		(PlanXZ,-100) 

	s1	=	CreateSurface	(PlanXZ) 
			AddVertex		(s1,0,0,0,0,0) 
			AddVertex		(s1,1,0,0,1,0) 
			AddVertex		(s1,0,0,1,0,1) 
			AddTriangle		(s1,0,1,2) 
			AddTriangle		(s1,0,2,1)
 
	PlanXY=	CreateMesh		(Axe) 
			EntityPickMode	(PlanXY,2)	; Fond 
			EntityColor		(PlanXY,000,255,000) 
			EntityFX		(PlanXY,1+8) 
			EntityAlpha		(PlanXY,.5) 
			NameEntity		(PlanXY,"[AXE]XY") 
			EntityTexture	(PlanXY,tex) 
			EntityOrder		(PlanXY,-100) 

	s2	=	CreateSurface	(PlanXY) 
			AddVertex		(s2,0,0,0,0,0) 
			AddVertex		(s2,1,0,0,1,0) 
			AddVertex		(s2,0,1,0,0,1) 
			AddTriangle		(s2,0,1,2) 
			AddTriangle		(s2,0,2,1) 

	PlanYZ=	CreateMesh		(Axe) 
			EntityPickMode	(PlanYZ,2) ; profil 
			EntityColor		(PlanYZ,000,000,255) 
			EntityFX		(PlanYZ,1+8) 
			EntityAlpha		(PlanYZ,.5) 
			NameEntity		(PlanYZ,"[AXE]YZ") 
			EntityTexture	(PlanYZ,tex) 
			EntityOrder		(PlanYZ,-100) 

	s3	=	CreateSurface	(PlanYZ) 
			AddVertex		(s3,0,0,0,0,0) 
			AddVertex		(s3,0,1,0,1,0) 
			AddVertex		(s3,0,0,1,0,1) 
			AddTriangle		(s3,0,1,2) 
			AddTriangle		(s3,0,2,1) 

	AxeX=	CreateCube		(Axe) 
			EntityColor		(AxeX,255,000,000) 
			EntityFX		(AxeX,1+8) 
			EntityPickMode	(AxeX,2) 
			EntityAlpha		(AxeX,.5) 
			ScaleMesh		(AxeX,.75,.025,.025) 
			PositionMesh	(AxeX,.75,0,0) 
			NameEntity		(AxeX,"[AXE]X") 
			EntityOrder		(AxeX,-100) 

	AxeY=	CreateCube		(Axe) 
			EntityColor		(AxeY,000,255,000) 
			EntityFX		(AxeY,1+8) 
			EntityPickMode	(AxeY,2) 
			EntityAlpha		(AxeY,.5) 
			ScaleMesh		(AxeY,.025,.75,.025) 
			PositionMesh	(AxeY,0,.75,0) 
			NameEntity		(AxeY,"[AXE]Y") 
			EntityOrder		(AxeY,-100) 

	AxeZ=	CreateCube		(Axe) 
			EntityColor		(AxeZ,000,000,255) 
			EntityFX		(AxeZ,1+8) 
			EntityPickMode	(AxeZ,2) 
			EntityAlpha		(AxeZ,.5) 
			ScaleMesh		(AxeZ,.025,.025,.75) 
			PositionMesh	(AxeZ,0,0,.75) 
			NameEntity		(AxeZ,"[AXE]Z") 
			EntityOrder		(AxeZ,-100) 

	Return Axe 
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D