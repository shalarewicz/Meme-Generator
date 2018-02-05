package memely;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.List;

public class TopOverlay implements Expression {

private final Expression bottom, top;
	
	public TopOverlay(Expression bottom, Expression top) {
		this.bottom = bottom;
		this.top = top;
	}
	
	@Override
	public String toString() {
		return bottom.toString() + "^" + top.toString();
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof TopOverlay && sameParts((TopOverlay) that);
	}
		
	private boolean sameParts(TopOverlay that) {
		return this.top.equals(that.top) && this.bottom.equals(that.bottom);		
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	@Override
	public List<Expression> getExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getHeight() {
		int height;
		
		final int topWidth = this.top.getWidth();
		final int topHeight = top.getHeight();
				
		final int bottomWidth = this.bottom.getWidth();
		final int bottomHeight = bottom.getHeight();
		
		if (topWidth < bottomWidth) {
			final double aspectRatio = bottomHeight / (double) bottomWidth;
			double newBottomHeight = aspectRatio * topWidth;
			height = Math.max(topHeight, (int) newBottomHeight);
		} else {
			final double aspectRatio = topHeight / (double) topWidth;
			final double newTopHeight = aspectRatio * bottomWidth;
			height = Math.max( (int) newTopHeight, bottomHeight);
		}
		return height;
	}

	@Override
	public int getWidth() {
		final int topWidth = this.top.getWidth();
		final int bottomWidth = this.bottom.getWidth();
		return Math.min(topWidth, bottomWidth);
	}

	@Override
	public Expression layout() {
		final int topWidth = this.top.getWidth();
		final int bottomWidth = this.bottom.getWidth();
		
		Expression top = this.top;
		Expression bottom = this.bottom;
		
		int width = this.getWidth();
		int height = this.getHeight();
		
		if (topWidth < bottomWidth) {
			top = this.top;
			final double aspectRatio = bottom.getHeight() / (double) bottomWidth;
			double newBottomHeight = aspectRatio * topWidth;
			bottom = new Rescale(bottom, width, (int) newBottomHeight);
			
		} else {
			bottom = this.bottom;
			final double aspectRatio = top.getHeight() / (double) topWidth;
			final double newTopHeight = aspectRatio * bottomWidth;
			top = new Rescale(top, width, (int) newTopHeight);
		}
		return new Rescale(new TopOverlay(bottom.layout(), top.layout()),width, height);
	}

	@Override
	public BufferedImage generate() throws IOException {
		final int upperLeftX = 0;
		final int upperLeftY = 0;
		final int outputWidth = this.getWidth();
		final int outputHeight = this.getHeight();
		
		final BufferedImage topImage = this.top.generate();
		final BufferedImage bottomImage = this.bottom.generate();
		
		final BufferedImage output = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics graphics = output.getGraphics();
        
        final ImageObserver NO_OBSERVER_NEEDED = null;
        
        graphics.drawImage(bottomImage, 
                upperLeftX, upperLeftY,
                outputWidth, this.bottom.getHeight(), 
                NO_OBSERVER_NEEDED);
        graphics.drawImage(topImage, 
                upperLeftX, upperLeftY,
                outputWidth, this.top.getHeight(), 
                NO_OBSERVER_NEEDED);
			
        return output;
	}

}
