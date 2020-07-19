Dialog for selecting a Font (name, style, size). Lists font family, associated fonts, styles, and
sizes. Sizes maybe selected with a list, entering size in a field, or using a slider. Setting a
font will update all sliders and sample text.

<b>Example</b><pre>
	final Font intialFont = new Font(FontUtils.COURIER, Font.BOLD, 24);
	final FontChooserDialog dialog =  new FontChooserDialog(null, "Font", intialFont).showDialog();
	Font newFont = dialog.getSelectedFont();
</pre>
<b>Build Requirements</b>
<pre>
	Java 11

	&lt;dependency&gt;
		&lt;groupId&gt;com.google.code.findbugs&lt;/groupId&gt;
		&lt;artifactId&gt;jsr305&lt;/artifactId&gt; &lt;!--  javax.annotations --&gt;
		&lt;version&gt;3.0.2&lt;/version&gt;
	&lt;/dependency&gt;
</pre>
<b>Screenshot</b>
<img src="doc-files/FontChooserDialog.jpg" width="100%" alt="FontChooserDialog.jpg">
</p>