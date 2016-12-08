<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script type="text/javascript">
var tiki_cookie_jar = new Array();
tiki_cookie_jar = {};
</script>
<script type="text/javascript" src="lib/tiki-js.js"></script>


<link rel="StyleSheet" href="lib/1.css" type="text/css">
<link rel="StyleSheet" href="styles/cindydoc.css" type="text/css">

<link rel="alternate" type="application/xml" title="RSS Wiki"  >
<style type="text/css">
<!--
.STYLE1 {font-size: 14px}
-->
</style>
</head><body class="tiki_wiki">

<div id="overDiv" style="position: absolute; visibility: hidden; z-index: 1000;"></div>
<script type="text/javascript" language="JavaScript" src="what_is_jgex_files/overlib.js"></script>

<div id="tiki-main">
    <div id="tiki-mid">
  <table id="tiki-midtbl" border="0" cellpadding="0" cellspacing="0">
      <tbody><tr>
            <td id="centercolumn" valign="top"><div id="tiki-center">


<h1><a class="pagetitle" title="refresh" accesskey="2" href="http://doc.cinderella.de/tiki-index.php?page=What+Is+Cinderella%3F">What Is JGEX?</a></h1>

<div class="wikitopline"></div>

<div class="wikitext">
  <div width="30%" height="90" style="width: 45%; color: rgb(0, 0, 0);"><span style="width: 280px; text-align: center; white-space: nowrap; color: rgb(0, 0, 0);"><img src="http://www.cs.wichita.edu/~ye/images/headline1.jpg" alt="Java Geometry Expert" width="218" height="117" /></span></div>
  <p>JGEX  is a software which combines dynamic geometry software (DGS), automated  geometry theorem prover (GTP) and our approach for visually dynamic  presentation of proofs. As a dynamic geometry software, JGEX can be  used to build dynamic visual models to assist teaching and learning of  various mathematical concepts. As an automated reasoning software, we  can build dynamic logic models which can do reasoning themselves. As a  tool for dynamic presentation of proofs, JGEX is a valuable for  teachers and students to write and present proofs of geometry theorems  with various dynamic visual effects.</p>
  <ol>
    <li>JGEX is a powerful software for <em>geometric reasoning</em>.  Within its domain, it invites comparison with the best of human  geometry provers. It implements most of the effective methods for  geometric reasoning introduced in the past twenty years, including the  deductive base method, Wu's method, and the full-angle method, etc.  With these methods, users may automated prove geometry theorems, to  discover new properties of theorems, and to generate readable proofs  for many geometry theorems. </li>
    <li> By its dynamic  nature, the diagram built by this softwares can be changed dynamically.  With JGEX, we can drag part of the diagram with mouse and see  immediately how the diagram changes accordingly.</li>
    <li>JGEX  can be used to create proofs either manually and automatically. It  provides a series of visual effects for presenting of these proof.</li>
  </ol>
  <p><strong>JGEX</strong> consists of three parts: <strong>the drawing part</strong>, <strong>the reasoning and                proving part</strong>, and the <strong>part of the visual presentation of proofs</strong>. In                the drawing part, JGEX provides a graphical interface for the user                to draw the diagram step by step with predefined constructions. Wu's                method, the Full Angle Method and the Deductive Database Method                based on Full Angle are implemented in JGEX as  reasoning and                proving tool. </p>
  <p>The part of visual presentation of proofs makes JGEX most                distinctive from other geometry drawing systems on one side, and                from other geometry reasoning systems, including our previous                versions of <strong>GEX</strong>, on the other side. It is based on our work on                automated generation of readable proofs and on our approach to                geometric drawing.</p>
  <p>&nbsp;</p>
  <h3><strong>1. A Dynamic Geometry Software </strong> <a name="dgs" id="dgs"></a></h3>
  <p>There have been excellent commercial geometry theorem drawing systems such as the Geometer’s          Sketchpad in the US, Cabri in France, and Cinderella in Germany. All of them are          capable of doing dynamic geometry. Each of them has its own advantages and extends to other          areas such as drawing in 3D geometry, etc.          The name “dynamic geometry” was introduced as early as 1950 in the book:<br />
  </p>
  <blockquote>
    <p align="center" class="codelisting STYLE1">By a dynamic geometry we simply mean a study of the parts of space andtheir relations            to one another while they are in motion and changing.</p>
  </blockquote>
  <p> The drawing part of JGEX allows the user to construct the diagram interactively and manipulate          the diagram in a dynamic way, so JGEX is first a DGS. Starting from free points, the user can          create elements which is dependent on existed elements. With the mouse, the user can place points,          draw lines, introduce marks, etc. In this way, the diagram is constructed step by step. Much more          important is the fact that the user can explore the dynamic nature of the diagram. The user can drag           part of the diagram with mouse and see immediately how the diagram changes accordingly.          However, JGEX has its distinctive features comparing to the three commercial geometry drawing          systems.</p>
  <p><a href="dynamic_geometry_software.html">See Detail &gt;&gt;&gt;</a> </p>
  <h3>2. An Automated Geometry Theorem Prover <a name="gtp" id="gtp"></a></h3>
  <p>Wu's          method, the Full Angle Method and the Deductive Database Method          based on Full Angle are implemented in JGEX as  reasoning and          proving tool.</p>
  <p><a href="automated_theorem_prover.html">See Detail &gt;&gt;&gt; </a></p>
  <h3>3. A Tool for Visual Presentation of Proofs <a name="vpp" id="vpp"></a></h3>
  <p>The part of visual presentation of proofs makes JGEX most           distinctive from other geometry drawing systems on one side, and           from other geometry reasoning systems, including our previous          versions of GEX, on the other side. It is based on our work on          automated generation of readable proofs and on our approach to          geometric drawing.</p>
  <p>However,  as a first step, instead of automated generation of visual  presentations of proofs, we implement the manual input method for  creating visual presentations of proofs. This gives us first-hand  experience with the approach we propose. It is also an important  preparation for our future work on the proof checker. Especially, we  have a collection of over 100 examples created manually with JGEX. We  collect mainly those examples that do not mix algebraic expressions or  computations with the geometry diagrams.</p>
  <p><a href="dynamic_presentation_of_proofs.html">See Detail &gt;&gt;&gt; </a></p>
  <p>&nbsp;</p>
  <p><strong>See Also: </strong></p>
  <ul>
    <li><a title="no description" href='gex_jgex.html' class='wiki'>GEX and JGEX </a></li>
    <li> <a title="no description" href='dynamic_geometry_software.html' class='wiki'>A Dynamic Geometry Software</a></li>
    <li> <a title="no description" href='automated_theorem_prover.html' class='wiki'>An Automated
      Geometry Theorem Prover</a></li>
    <li> <a title="no description" href='dynamic_presentation_of_proofs.html' class='wiki'>A  Tool for  Visually Dynamic
      Presentation of Proofs</a></li>
  </ul>
  <p>&nbsp;</p>
</div>
